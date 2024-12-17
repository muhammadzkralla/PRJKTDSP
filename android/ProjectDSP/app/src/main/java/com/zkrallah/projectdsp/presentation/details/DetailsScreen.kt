package com.zkrallah.projectdsp.presentation.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zkrallah.projectdsp.service.BluetoothLeService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    detailsViewModel: DetailsViewModel = hiltViewModel(),
    bluetoothLeService: BluetoothLeService? = null,
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Home", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFFD9D9D9)
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = {}, content = {
            Icon(
                imageVector = Icons.Rounded.ExitToApp, contentDescription = "Disconnect"
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

        }
    }
}

@Preview
@Composable
fun Preview() {
    DetailsScreen()
}