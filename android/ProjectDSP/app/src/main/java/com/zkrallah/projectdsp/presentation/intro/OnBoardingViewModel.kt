package com.zkrallah.projectdsp.presentation.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zkrallah.projectdsp.domain.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    private val _startingDestination: MutableStateFlow<String> = MutableStateFlow("onboarding")
    val startingDestination: StateFlow<String> = _startingDestination

    suspend fun getStartingDestination() {
        val isOnboardingDone = mainRepository.getOnBoardingDone()
        if (isOnboardingDone) {
            _startingDestination.emit("home")
        }
    }

    fun setOnBoardingStatus() {
        viewModelScope.launch {
            mainRepository.setOnBoardingDone()
        }
    }
}