package com.zkrallah.projectdsp.di

import com.zkrallah.projectdsp.data.dataStore.DataStore
import com.zkrallah.projectdsp.data.repositories.MainRepositoryImpl
import com.zkrallah.projectdsp.domain.repositories.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Provides
    @Singleton
    fun provideMainRepository(
        dataStore: DataStore
    ): MainRepository {
        return MainRepositoryImpl(dataStore)
    }
}