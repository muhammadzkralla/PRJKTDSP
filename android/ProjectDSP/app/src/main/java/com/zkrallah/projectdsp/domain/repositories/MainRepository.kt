package com.zkrallah.projectdsp.domain.repositories

interface MainRepository {
    suspend fun getOnBoardingDone(): Boolean

    suspend fun setOnBoardingDone()
}