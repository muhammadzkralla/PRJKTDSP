package com.zkrallah.projectdsp.data.repositories

import kotlinx.coroutines.flow.MutableStateFlow

object BleRepositoryImpl {
    val connectionStatus = MutableStateFlow(false)
    val notifiableData = MutableStateFlow("")
    val readableData = MutableStateFlow("")
}
