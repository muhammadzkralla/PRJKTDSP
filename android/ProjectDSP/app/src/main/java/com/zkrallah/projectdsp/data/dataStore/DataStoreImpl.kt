package com.zkrallah.projectdsp.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zkrallah.projectdsp.domain.models.Token
import com.zkrallah.projectdsp.domain.models.User
import com.zkrallah.projectdsp.domain.models.toJson
import com.zkrallah.projectdsp.domain.models.toUserModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.dataStore by preferencesDataStore("user_data")

class DataStoreImpl(
    appContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataStore {

    private val mDataStore by lazy {
        appContext.dataStore
    }

    companion object {
        const val ON_BOARDING = "onBoarding"
        const val IS_LOGGED = "isLogged"
        const val TOKEN = "token"
        const val USER = "user"
    }

    override suspend fun saveUserModel(userModel: User?) {
        mDataStore.edit { preferences ->
            preferences[stringPreferencesKey(USER)] = userModel?.toJson() ?: ""
        }
    }

    override suspend fun getUserModel(): User = withContext(dispatcher) {
        mDataStore.data.map { preferences ->
            try {
                val string = preferences[stringPreferencesKey(USER)] ?: ""
                string.toUserModel()
            } catch (e: Exception) {
                null
            }
        }.first()!!
    }

    override suspend fun getIsOnBoardingFinished(): Boolean = withContext(dispatcher) {
        mDataStore.data.map { settings ->
            settings[booleanPreferencesKey(ON_BOARDING)] ?: false
        }.first()
    }

    override suspend fun setIsOnBoardingFinished(isOnBoardingFinished: Boolean) {
        withContext(dispatcher) {
            mDataStore.edit { settings ->
                settings[booleanPreferencesKey(ON_BOARDING)] = isOnBoardingFinished
            }
        }
    }

    override suspend fun getIsLoggedIn(): Boolean = withContext(dispatcher) {
        mDataStore.data.map { settings ->
            settings[booleanPreferencesKey(IS_LOGGED)] ?: false
        }.first()
    }

    override suspend fun setIsLoggedIn(isLogged: Boolean) {
        withContext(dispatcher) {
            mDataStore.edit { settings ->
                settings[booleanPreferencesKey(IS_LOGGED)] = isLogged
            }
        }
    }

    override suspend fun insertToken(data: Token) {
        withContext(dispatcher) {
            saveUserModel(data.user)
            mDataStore.edit { settings ->
                settings[stringPreferencesKey(TOKEN)] = data.accessToken!!
            }
        }
    }

    override suspend fun getToken(): String = withContext(dispatcher) {
        mDataStore.data.map { settings ->
            settings[stringPreferencesKey(TOKEN)] ?: ""
        }.first()
    }

    override suspend fun logOut() {
        setIsLoggedIn(false)
    }

}