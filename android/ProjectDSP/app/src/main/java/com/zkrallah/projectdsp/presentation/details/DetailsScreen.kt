package com.zkrallah.projectdsp.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zkrallah.projectdsp.R
import com.zkrallah.projectdsp.presentation.dialog.InformationDialog
import com.zkrallah.projectdsp.service.BluetoothLeService
import com.zkrallah.projectdsp.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    detailsViewModel: DetailsViewModel = hiltViewModel(),
    bluetoothLeService: BluetoothLeService? = null,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current

    val showDialog = remember { mutableStateOf(false) }
    val dialogData = remember { mutableStateOf<String?>(null)}

    val dhtNotifiable = remember { mutableStateOf(true) }
    val heartNotifiable = remember { mutableStateOf(true) }

    val humidity by detailsViewModel.humidity.collectAsState()
    val temp by detailsViewModel.temp.collectAsState()
    val heartRate by detailsViewModel.heartRate.collectAsState()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Home", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFFD9D9D9)
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            bluetoothLeService?.disconnect()
            navController.navigate("home")
        }, content = {
            Image(
                painter = painterResource(id = R.drawable.disconnect), contentDescription = "Disconnect"
            )
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Row {
                        Image(
                            painter = if (dhtNotifiable.value)
                                painterResource(id = R.drawable.ic_bell_on)
                            else
                                painterResource(id = R.drawable.ic_bell_off),
                            contentDescription = "Notifiable",
                            modifier = Modifier.clickable(onClick = {
                                dhtNotifiable.value = !dhtNotifiable.value
                                if (dhtNotifiable.value)
                                    showToast(context, "DHT Notifiable Enabled")
                                else
                                    showToast(context, "DHT Notifiable Disabled")
                            })
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "DHT",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W400,
                                fontStyle = FontStyle.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Column {
                            Text(
                                text = "Humidity",
                                color = Color(0xFF0C098C),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Image(
                                painter = painterResource(id = R.drawable.humidity),
                                contentDescription = "",
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(33.dp)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (dhtNotifiable.value) humidity else "-",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }

                        Spacer(modifier = Modifier.weight(0.1f))

                        Column {
                            Text(
                                text = "Temperature",
                                color = Color(0xFFF88C11),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Image(
                                painter = painterResource(id = R.drawable.temp),
                                contentDescription = "",
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(33.dp)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (dhtNotifiable.value) temp else "-",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(onClick = {
                            showDialog.value = true
                            dialogData.value = "DHT Sensor"
                        }) {
                            Text("view more", fontSize = 12.sp)
                        }
                    }
                }

            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9))
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Row {
                        Image(
                            painter = if (heartNotifiable.value)
                                painterResource(id = R.drawable.ic_bell_on)
                            else
                                painterResource(id = R.drawable.ic_bell_off),
                            contentDescription = "Notifiable",
                            modifier = Modifier.clickable(onClick = {
                                heartNotifiable.value = !heartNotifiable.value
                                if (heartNotifiable.value)
                                    showToast(context, "Heart Rate Notifiable Enabled")
                                else
                                    showToast(context, "Heart Rate Notifiable Disabled")
                            })
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "Heart Rate",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W400,
                                fontStyle = FontStyle.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Image(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = "",
                        modifier = Modifier
                            .width(44.dp)
                            .height(33.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (heartNotifiable.value) heartRate else "-",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(onClick = {
                            showDialog.value = true
                            dialogData.value = "Heart Rate Sensor"
                        }) {
                            Text("view more", fontSize = 12.sp)
                        }
                    }
                }

            }
        }

        if (showDialog.value && dialogData.value != null) {
            InformationDialog(title = dialogData.value!!) {
                showDialog.value = false
                dialogData.value = null
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    DetailsScreen()
}