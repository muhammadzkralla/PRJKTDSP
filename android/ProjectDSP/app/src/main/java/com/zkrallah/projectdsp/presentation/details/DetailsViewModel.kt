package com.zkrallah.projectdsp.presentation.details

import androidx.lifecycle.ViewModel
import com.zkrallah.projectdsp.data.repositories.BleRepositoryImpl
import kotlinx.coroutines.flow.StateFlow

class DetailsViewModel : ViewModel() {
    val humidity: StateFlow<String> = BleRepositoryImpl.humidity
    val temp: StateFlow<String> = BleRepositoryImpl.temp
    val heartRate: StateFlow<String> = BleRepositoryImpl.heartRate
}