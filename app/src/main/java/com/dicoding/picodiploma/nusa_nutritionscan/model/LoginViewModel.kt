package com.dicoding.picodiploma.nusa_nutritionscan.model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.dicoding.picodiploma.nusa_nutritionscan.API.ApiConfig
import com.dicoding.picodiploma.nusa_nutritionscan.data.Data
import com.dicoding.picodiploma.nusa_nutritionscan.data.LogInResponse
import com.dicoding.picodiploma.nusa_nutritionscan.data.SignUpResponse
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel (private val pref: UserPreferenceDatastore): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    val error = MutableLiveData("")
    val message = MutableLiveData("")
    private val tag = LoginViewModel::class.simpleName

    val loginResult = MutableLiveData<LogInResponse>()

    fun getUser(): LiveData<Data> {
        return pref.getUser().asLiveData()
    }

    fun saveUser(userName: String, userId: String, userToken: String, freshToken: String) {
        viewModelScope.launch {
            pref.saveUser(userName, userId, userToken, freshToken)
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val data = mapOf(
            "email" to email,
            "password" to password,
        )
        val gson = Gson()
        val jsonData = gson.toJson(data)

        val requestBody = jsonData.toRequestBody("application/json".toMediaType())
        val client = ApiConfig.getApiService().Login(requestBody)
        client.enqueue(object : Callback<LogInResponse> {
            override fun onResponse(call: Call<LogInResponse>, response: Response<LogInResponse>) {
                when (response.code()) {
                    200 -> {
                        loginResult.postValue(response.body())
                        message.postValue("200")
                    }
                    400 -> error.postValue("400")
                    401 -> error.postValue("401")
                    else -> error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<LogInResponse>, t: Throwable) {
                _isLoading.value = true
                Toast.makeText(null, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun logout(token: String){
        viewModelScope.launch {
            pref.signout()
        }
        val client = ApiConfig.getApiService().Logout(bearer = "Bearer $token")
        client.enqueue(object : Callback<SignUpResponse>{
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                when(response.code()){
                    200 -> {
                        message.postValue("200")
                    }
                    400 -> error.postValue("400")
                    401 -> error.postValue("401")
                    else -> error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun refresh(token: String){
        val data = mapOf(
            "refresh_token" to token.toString()
        )
        val gson = Gson()
        val jsonData = gson.toJson(data)
        print(data)
        val requestBody = jsonData.toRequestBody("application/json".toMediaType())
        val client = ApiConfig.getApiService().Refresh(requestBody)
        client.enqueue(object : Callback<LogInResponse>{
            override fun onResponse(call: Call<LogInResponse>, response: Response<LogInResponse>) {
                if (response.isSuccessful){
                    loginResult.postValue(response.body())
                }
                else{
                    Log.e(tag, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LogInResponse>, t: Throwable) {
                Log.e(tag, "onFailure: ${t.message}")
            }
        })
    }
}