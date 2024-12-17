package com.zkrallah.projectdsp.presentation.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zkrallah.projectdsp.service.BluetoothLeService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    devices: List<BluetoothDevice> = emptyList(),
    bluetoothLeService: BluetoothLeService? = null,
    scan: () -> Unit = {}
) {
    val data = remember { mutableStateOf(TextFieldValue()) }
    val connectionStatus by homeViewModel.connectionStatus.collectAsState()
    val notifiableData by homeViewModel.notifiableData.collectAsState()
    val readableData by homeViewModel.readableData.collectAsState()
    val isScanning by homeViewModel.isScanning.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DSP NeoVim") },
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Connection Status Section
//            ConnectedDeviceSection(
//                deviceName = if (connectionStatus) "Connected" else "Disconnected",
//                onDisconnect = { bluetoothLeService?.disconnect() }
//            )

            Spacer(modifier = Modifier.height(16.dp))

            // Device List Section
            if (isScanning) {
                Text(
                    text = "Available Devices:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(devices) { device ->
                        DeviceItem(device, bluetoothLeService)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Scan Button
            if (!connectionStatus && !isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center // Center the button in the screen
                ) {
                    Column {
                        Text(
                            text = "Click to start searching for devices",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { scan() },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Text("Search")
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Data Transfer Section
            if (connectionStatus) {
                Text(
                    text = "Notifications:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notifiableData,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = data.value,
                        onValueChange = { data.value = it },
                        label = { Text("Enter Data") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            BluetoothLeService.chars["abcd4321-ab12-ab12-ab12-ab1234567890"]?.let {
                                bluetoothLeService?.writeCharacteristic(it, data.value.text)
                            }
                        }
                    ) {
                        Text("SEND")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = readableData,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            BluetoothLeService.chars["1234abcd-ab12-ab12-ab12-ab1234567890"]?.let {
                                bluetoothLeService?.readCharacteristic(it)
                            }
                        }
                    ) {
                        Text("RECEIVE")
                    }
                }
            }
        }
    }
}


@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice, service: BluetoothLeService?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { connectToDevice(device.address, service) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = device.name ?: "Unknown Device",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = device.address, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ConnectedDeviceSection(deviceName: String, onDisconnect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Connection Status: $deviceName", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { onDisconnect() }) {
                Text("Disconnect")
            }
        }
    }
}

private fun connectToDevice(address: String, bluetoothLeService: BluetoothLeService?) {
    bluetoothLeService?.let { service ->
        if (service.connect(address)) {
            Log.d(TAG, "Connecting to device: $address")
        } else {
            Log.e(TAG, "Failed to connect to device: $address")
        }
    } ?: run {
        Log.e(TAG, "BluetoothLe Service is not available.")
    }
}

@Preview("")
@Composable
fun Preview() {
    HomeScreen()
}

const val TAG = "HomeScreen"
