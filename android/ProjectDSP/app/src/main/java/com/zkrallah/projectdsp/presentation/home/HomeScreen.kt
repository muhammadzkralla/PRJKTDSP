package com.zkrallah.projectdsp.presentation.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zkrallah.projectdsp.service.BluetoothLeService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    devices: List<BluetoothDevice> = emptyList(),
    bluetoothLeService: BluetoothLeService? = null,
    scan: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val connectionStatus by homeViewModel.connectionStatus.collectAsState()
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available Devices:",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

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
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
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
                navController.navigate("details")
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
