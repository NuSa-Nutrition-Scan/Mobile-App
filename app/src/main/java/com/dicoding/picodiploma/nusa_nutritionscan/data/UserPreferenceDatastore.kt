package com.dicoding.picodiploma.nusa_nutritionscan.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("User")

class UserPreferenceDatastore private constructor(private val dataStore: DataStore<Preferences>){

    fun getToken(): Flow<String> = dataStore.data.map { it[TOKEN_KEY] ?: "Tidak diatur" }

    suspend fun saveUser(
        userName: String,
        userId: String,
        userToken: String,
        freshToken: String,
        sex: String,
        weight: Int,
        eaten: Int,
        cal: Int,
        update: Boolean,
        expired: String,
        age: Int,
        email: String,
        height: Int
    ) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = userName
            preferences[USERID_KEY] = userId
            preferences[TOKEN_KEY] = userToken
            preferences[FRESH_TOKEN] = freshToken
            preferences[SEX] = sex
            preferences[WEIGHT] = weight
            preferences[EATEN] = eaten
            preferences[CALORIES] = cal
            preferences[UPDATED] = update
            preferences[EXPIRED] = expired
            preferences[AGE] = age
            preferences[EMAIL] = email
            preferences[HEIGHT] = height
        }
    }

    fun getUser(): Flow<Data> {
        return dataStore.data.map { preferences ->
            Data(
                preferences[SEX] ?: "",
                preferences[WEIGHT] ?: 0,
                preferences[TOKEN_KEY] ?: "",
                preferences[FRESH_TOKEN] ?: "",
                preferences[EATEN] ?: 3,
                preferences[CALORIES] ?: 0,
                preferences[UPDATED] ?: false,
                preferences[NAME_KEY] ?: "",
                preferences[USERID_KEY]?: "",
                preferences[EXPIRED]?:"",
                preferences[AGE]?: 0,
                preferences[EMAIL]?:"",
                preferences[HEIGHT]?:0
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
            preferences[SEX] = ""
            preferences[WEIGHT] = 0
            preferences[EATEN] = 3
            preferences[CALORIES] = 0
            preferences[UPDATED] = false
            preferences[AGE] = 0
            preferences[EMAIL] = ""
            preferences[HEIGHT] = 0
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: UserPreferenceDatastore? = null

        private var NAME_KEY = stringPreferencesKey("name")
        private val USERID_KEY = stringPreferencesKey("id")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val EXPIRED = stringPreferencesKey("expired")
        private val EMAIL = stringPreferencesKey("email")
        private val FRESH_TOKEN = stringPreferencesKey("fresh_token")
        private val SEX = stringPreferencesKey("sex")
        private val WEIGHT = intPreferencesKey("weight")
        private val EATEN = intPreferencesKey("eat_per_day")
        private val CALORIES = intPreferencesKey("calories_target")
        private val UPDATED = booleanPreferencesKey("has_been_updated")
        private val AGE = intPreferencesKey("age")
        private val HEIGHT = intPreferencesKey("height")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferenceDatastore {
            return INSTANCE ?: synchronized(this){
                val instance = UserPreferenceDatastore(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}