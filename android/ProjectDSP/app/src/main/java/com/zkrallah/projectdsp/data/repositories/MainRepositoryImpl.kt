package com.zkrallah.projectdsp.data.repositories

import com.zkrallah.projectdsp.data.dataStore.DataStore
import com.zkrallah.projectdsp.domain.repositories.MainRepository

class MainRepositoryImpl(
    private val dataStore: DataStore
) : MainRepository {
    override suspend fun getOnBoardingDone(): Boolean {
        return dataStore.getIsOnBoardingFinished()
    }

    override suspend fun setOnBoardingDone() {
        dataStore.setIsOnBoardingFinished(true)
    }
}