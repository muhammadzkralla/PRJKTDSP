package com.zkrallah.projectdsp.service

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.zkrallah.projectdsp.R
import com.zkrallah.projectdsp.data.repositories.BleRepositoryImpl
import java.util.UUID

/**
 * A service for managing Bluetooth Low Energy (BLE) connections and interactions.
 * This service handles connecting to a BLE device, discovering services, reading/writing characteristics,
 * and handling notifications.
 */
@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
class BluetoothLeService : Service() {

    private var bluetoothAdapter: BluetoothAdapter? = null

    private var bluetoothGatt: BluetoothGatt? = null

    private var connectionState = STATE_DISCONNECTED

    private val binder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "CHANNEL_1")
            .setContentTitle("BLE Service Running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            )
        } else {
            startForeground(1, notification)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources
        stopForeground(true)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Stop the service when app is removed from memory
        stopForeground(true)
        stopSelf()
    }

    /**
     * Binder class to expose the service to clients.
     */
    inner class LocalBinder : Binder() {
        /**
         * Provides the service instance to the client.
         * @return The BluetoothLeService instance.
         */
        fun getService() : BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    /**
     * Callback for GATT events, such as connection changes, characteristic reads/writes, and notifications.
     */
    private val bluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {

        /**
         * Called when the connection state changes (e.g., connected or disconnected).
         * @param gatt The GATT client involved in the connection change.
         * @param status The status of the operation.
         * @param newState The new connection state.
         */
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                connectionState = STATE_CONNECTED
                BleRepositoryImpl.connectionStatus.value = true

                // Attempts to discover services after successful connection.
                bluetoothGatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                stopSelf()
                connectionState = STATE_DISCONNECTED
                BleRepositoryImpl.connectionStatus.value = false
            }
        }

        /**
         * Called when services are discovered on the remote device.
         * @param gatt The GATT client involved in the discovery.
         * @param status The status of the discovery operation.
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                displayGattServices(getSupportedGattServices())
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        /**
         * Called when a characteristic is read from the remote device.
         * @param gatt The GATT client involved in the operation.
         * @param characteristic The characteristic that was read.
         * @param status The status of the read operation.
         */
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onCharacteristicRead: ${characteristic.uuid} with ${String(characteristic.value, Charsets.UTF_8)}")
                BleRepositoryImpl.readableData.value = String(characteristic.value, Charsets.UTF_8)
            } else Log.d(TAG, "onCharacteristicRead: Failed to read characteristic")
        }

        /**
         * Called when a notifiable characteristic is updated on the remote device.
         * @param gatt The GATT client involved in the operation.
         * @param characteristic The characteristic that was updated.
         */
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            Log.d(TAG, "onCharacteristicChanged: ${characteristic.uuid} with ${String(characteristic.value, Charsets.UTF_8)}")
            when (characteristic.uuid.toString()) {
                "abcd1234-ab12-ab12-ab12-ab1234567890" -> BleRepositoryImpl.humidity.value =
                    String(characteristic.value, Charsets.UTF_8)

                "abcd4321-ab12-ab12-ab12-ab1234567890" -> BleRepositoryImpl.temp.value =
                    String(characteristic.value, Charsets.UTF_8)

                "dcba1234-ab12-ab12-ab12-ab1234567890" -> BleRepositoryImpl.heartRate.value =
                    String(characteristic.value, Charsets.UTF_8)
            }
        }

        /**
         * Called when a characteristic is written to the remote device.
         * @param gatt The GATT client involved in the operation.
         * @param characteristic The characteristic that was written.
         * @param status The status of the write operation.
         */
        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onCharacteristicWrite: SUCCESS")
            } else {
                Log.d(TAG, "onCharacteristicWrite: FAILED")
            }
        }

        /**
         * Called when a descriptor is written to the remote device.
         * @param gatt The GATT client involved in the operation.
         * @param descriptor The descriptor that was written.
         * @param status The status of the write operation.
         */
        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Descriptor write successful. Now writing to characteristic.")
            } else {
                Log.w(TAG, "Descriptor write failed: $status")
            }
        }

    }

    /**
     * Initializes the Bluetooth adapter.
     * @return True if the adapter was successfully initialized, false otherwise.
     */
    fun initialize(): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    /**
     * Connects to a BLE device with the specified address.
     * @param address The MAC address of the device.
     * @return True if the connection was initiated successfully, false otherwise.
     */
    fun connect(address: String): Boolean {
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                // connect to the GATT server on the device
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
                Log.d(TAG, "Connected to device: $address")
                return true
            } catch (exception: IllegalArgumentException) {
                Log.w(TAG, "Device not found with provided address.  Unable to connect.")
                return false
            }
        } ?: run {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return false
        }
    }

    /**
     * Retrieves the GATT services supported by the connected device.
     * @return A list of supported GATT services, or null if no device is connected.
     */
    fun getSupportedGattServices(): List<BluetoothGattService?>? {
        return bluetoothGatt?.services
    }

    /**
     * Reads a characteristic from the connected device.
     * @param characteristic The characteristic to read.
     */
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        bluetoothGatt?.readCharacteristic(characteristic) ?: run {
            Log.w(TAG, "BluetoothGatt not initialized")
            return
        }
    }

    /**
     * Writes a value to a characteristic on the connected device.
     * @param characteristic The characteristic to write to.
     * @param payload The value to write, as a string.
     */
    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, payload: String) {
        val writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        bluetoothGatt?.let { gatt ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val tmp = gatt.writeCharacteristic(characteristic, payload.toByteArray(Charsets.UTF_8), writeType)
                Log.d(TAG, "writeCharacteristic: $tmp")
            } else {
                // Fall back to deprecated version of writeCharacteristic for Android <13
                gatt.legacyCharacteristicWrite(characteristic, payload.toByteArray(Charsets.UTF_8), writeType)
            }
        } ?: error("Not connected to a BLE device!")
    }

    @TargetApi(Build.VERSION_CODES.S)
    private fun BluetoothGatt.legacyCharacteristicWrite(
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        writeType: Int
    ) {
        characteristic.writeType = writeType
        characteristic.value = value
        writeCharacteristic(characteristic)
    }


    /**
     * Sets up notifications for a given characteristic.
     * @param characteristic The characteristic to enable notifications for.
     */
    private fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic
    ) {
        bluetoothGatt?.let { gatt ->
            gatt.setCharacteristicNotification(characteristic, true)

            // This is specific to Heart Rate Measurement.
            if (UUID.fromString("abcd1234-ab12-ab12-ab12-ab1234567890") == characteristic.uuid) {
                val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
        } ?: run {
            Log.w(TAG, "BluetoothGatt not initialized")
        }
    }

    /**
     * Disconnects from the currently connected BLE device.
     */
    fun disconnect() {
        Log.d(TAG, "Disconnected")
        bluetoothGatt?.disconnect()
    }

    /**
     * Displays the GATT services and their characteristics.
     * @param gattServices The list of discovered GATT services.
     */
    private fun displayGattServices(gattServices: List<BluetoothGattService?>?) {
        if (gattServices == null) return

        var uuid: String?

        // Loops through available GATT Services.
        gattServices.forEach { gattService ->
            uuid = gattService?.uuid.toString()

            val gattCharacteristics = gattService?.characteristics

            // Loops through available Characteristics.
            gattCharacteristics?.forEach { gattCharacteristic ->
                uuid = gattCharacteristic.uuid.toString()
                chars[uuid!!] = gattCharacteristic

                if (gattCharacteristic.properties == BluetoothGattCharacteristic.PROPERTY_READ) {
                    Log.d(TAG, "Characteristic UUID: $uuid in service ${gattService.uuid} is readable")
                }

                if (gattCharacteristic.properties == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                    Log.d(TAG, "Characteristic UUID: $uuid in service ${gattService.uuid} is notifiable")
                    setCharacteristicNotification(gattCharacteristic)
                }

                if (gattCharacteristic.properties == BluetoothGattCharacteristic.PROPERTY_WRITE) {
                    Log.d(TAG, "Characteristic UUID: $uuid in service ${gattService.uuid} is writable")
                }
            }
        }
    }

    /**
     * Closes the current GATT connection and releases resources.
     */
    private fun close() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
        }
    }

    companion object {
        const val TAG = "BluetoothLeService"

        // Stores characteristics by their UUIDs.
        val chars = mutableMapOf<String, BluetoothGattCharacteristic>()

        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 1
    }
}