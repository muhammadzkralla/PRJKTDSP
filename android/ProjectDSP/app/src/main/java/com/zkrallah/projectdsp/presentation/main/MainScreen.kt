package com.zkrallah.projectdsp.presentation.main

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.zkrallah.projectdsp.presentation.home.HomeScreen
import com.zkrallah.projectdsp.presentation.intro.OnBoarding
import com.zkrallah.projectdsp.presentation.intro.OnBoardingViewModel
import com.zkrallah.projectdsp.service.BluetoothLeService
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AppNavigation(
    onBoardingViewModel: OnBoardingViewModel = hiltViewModel(),
    devices: List<BluetoothDevice>,
    bluetoothLeService: BluetoothLeService?,
    scan: () -> Unit
) {
    runBlocking {
        onBoardingViewModel.getStartingDestination()
    }

    val startingDestination = onBoardingViewModel.startingDestination.collectAsState()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startingDestination.value) {
        composable("onboarding") { OnBoarding(navController = navController) }
        composable("home") { HomeScreen(devices = devices, bluetoothLeService = bluetoothLeService, scan = scan) }
    }
}
