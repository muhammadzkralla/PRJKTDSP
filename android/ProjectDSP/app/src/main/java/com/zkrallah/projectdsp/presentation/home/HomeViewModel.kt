package com.zkrallah.projectdsp.presentation.home

import androidx.lifecycle.ViewModel
import com.zkrallah.projectdsp.data.repositories.BleRepositoryImpl
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    val connectionStatus: StateFlow<Boolean> = BleRepositoryImpl.connectionStatus
    val notifiableData: StateFlow<String> = BleRepositoryImpl.notifiableData
    val readableData: StateFlow<String> = BleRepositoryImpl.readableData
}