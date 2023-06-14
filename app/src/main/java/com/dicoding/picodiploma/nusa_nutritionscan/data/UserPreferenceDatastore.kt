package com.dicoding.picodiploma.nusa_nutritionscan.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("User")

class UserPreferenceDatastore private constructor(private val dataStore: DataStore<Preferences>){

    fun getToken(): Flow<String> = dataStore.data.map { it[TOKEN_KEY] ?: "Tidak diatur" }

    suspend fun saveUser(userName: String, userId: String, userToken: String, freshToken: String) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = userName
            preferences[USERID_KEY] = userId
            preferences[TOKEN_KEY] = userToken
            preferences[FRESH_TOKEN] = freshToken
        }
    }

    fun getUser(): Flow<Data> {
        return dataStore.data.map { preferences ->
            Data(
                preferences[FRESH_TOKEN] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[USERID_KEY] ?: "",
                preferences[EXPIRED] ?: "",
                preferences[EMAIL] ?: "",
                preferences[TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun signout(){
        dataStore.edit { preferences ->
            preferences[FRESH_TOKEN] = ""
            preferences[NAME_KEY] = ""
            preferences[USERID_KEY] = ""
            preferences[EXPIRED] = ""
            preferences[EMAIL] = ""
            preferences[TOKEN_KEY] = ""
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: UserPreferenceDatastore? = null

        private var NAME_KEY = stringPreferencesKey("name")
        private val USERID_KEY = stringPreferencesKey("userId")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val EXPIRED = stringPreferencesKey("expired")
        private val EMAIL = stringPreferencesKey("email")
        private val FRESH_TOKEN = stringPreferencesKey("freshToken")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferenceDatastore {
            return INSTANCE ?: synchronized(this){
                val instance = UserPreferenceDatastore(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}