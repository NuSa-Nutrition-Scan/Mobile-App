package com.dicoding.picodiploma.nusa_nutritionscan.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.API.ApiConfig
import com.dicoding.picodiploma.nusa_nutritionscan.data.SignUpResponse
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel (private val pref: UserPreferenceDatastore): ViewModel(){
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val error = MutableLiveData("")
    val message = MutableLiveData("")
    private val tag = RegisterViewModel::class.java.simpleName

    fun register(name: String, email: String, password: String){
        val data = mapOf(
            "name" to name,
            "email" to email,
            "password" to password,
        )
        val gson = Gson()
        val jsonData = gson.toJson(data)

        val requestBody = jsonData.toRequestBody("application/json".toMediaType())
        _isLoading.value = true
        val client = ApiConfig.getApiService().Register(requestBody)
        client.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                when(response.code()){
                    400 -> error.postValue("400")
                    201 -> message.postValue("201")
                    else -> error.postValue("ERROR ${response.code()}: ${response.errorBody()}")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(tag, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }

        })
    }
}