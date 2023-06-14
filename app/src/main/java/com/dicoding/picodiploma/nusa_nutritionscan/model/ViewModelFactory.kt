package com.dicoding.picodiploma.nusa_nutritionscan.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore

class ViewModelFactory(private val pref: UserPreferenceDatastore) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(pref) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            else -> throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}