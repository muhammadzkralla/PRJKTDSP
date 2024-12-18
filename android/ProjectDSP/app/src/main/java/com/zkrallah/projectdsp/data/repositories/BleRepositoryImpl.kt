package com.zkrallah.projectdsp.data.repositories

import kotlinx.coroutines.flow.MutableStateFlow

object BleRepositoryImpl {
    val isScanning = MutableStateFlow(false)
    val connectionStatus = MutableStateFlow(false)
    val humidity = MutableStateFlow("")
    val temp = MutableStateFlow("")
    val heartRate = MutableStateFlow("")
    val notifiableData = MutableStateFlow("")
    val readableData = MutableStateFlow("")
}
