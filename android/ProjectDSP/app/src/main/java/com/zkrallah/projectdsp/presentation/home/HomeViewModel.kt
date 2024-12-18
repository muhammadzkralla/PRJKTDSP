package com.zkrallah.projectdsp.presentation.home

import androidx.lifecycle.ViewModel
import com.zkrallah.projectdsp.data.repositories.BleRepositoryImpl
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    val isScanning: StateFlow<Boolean> = BleRepositoryImpl.isScanning
    val connectionStatus: StateFlow<Boolean> = BleRepositoryImpl.connectionStatus
}