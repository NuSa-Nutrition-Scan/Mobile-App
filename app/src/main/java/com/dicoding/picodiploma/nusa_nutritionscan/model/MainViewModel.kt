package com.dicoding.picodiploma.nusa_nutritionscan.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.nusa_nutritionscan.API.ApiConfig
import com.dicoding.picodiploma.nusa_nutritionscan.API.ImageUploadResponse
import com.dicoding.picodiploma.nusa_nutritionscan.Activity.FoodRecommendResponse
import com.dicoding.picodiploma.nusa_nutritionscan.data.UserPreferenceDatastore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainViewModel (private val pref: UserPreferenceDatastore): ViewModel(){
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _foodDetection= MutableLiveData<FoodPredictionResponse>()
    val foodDetection: LiveData<FoodPredictionResponse> = _foodDetection

    private val _imageUpload = MutableLiveData<ImageUploadResponse>()
    val imageUpload: LiveData<ImageUploadResponse> = _imageUpload

    private val _listFood = MutableLiveData<List<FoodRecommendResponse>>()
    val listFood: LiveData<List<FoodRecommendResponse>> = _listFood

    companion object{
        private const val tag = "MainViewModel"
    }

    fun foodPrediction(token : String, file: File){
        _isLoading.value = true
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val client = ApiConfig.getApiService()

        val call = client.predictionFood(bearer = "Bearer $token", body)
        call.enqueue(object : retrofit2.Callback<FoodPredictionResponse>{
            override fun onFailure(call: retrofit2.Call<FoodPredictionResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(tag, "onFailure: ${t.message}")
            }

            override fun onResponse(call: retrofit2.Call<FoodPredictionResponse>, response: retrofit2.Response<FoodPredictionResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _foodDetection.postValue(response.body())
                    print("ini adalah : ${response.body()}")
                }
                else{
                    Log.e(tag, "onFailure: ${response.message()}")
                }
            }
        })
    }

    fun uploadImage(token : String, file: File){
        _isLoading.value = true
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val client = ApiConfig.getApiService()

        val call = client.uploadImage(bearer = "Bearer $token", body)
        call.enqueue(object : retrofit2.Callback<ImageUploadResponse>{
            override fun onFailure(call: retrofit2.Call<ImageUploadResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(tag, "onFailure: ${t.message}")
            }

            override fun onResponse(call: retrofit2.Call<ImageUploadResponse>, response: retrofit2.Response<ImageUploadResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _imageUpload.postValue(response.body())
                    print("ini adalah : ${response.body()}")
                }
                else{
                    Log.e(tag, "onFailure: ${response.message()}")
                }
            }
        })
    }

    fun foodRecommend(token: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getRecommend(bearer = "Bearer $token")
        client.enqueue(object : Callback<FoodRecommendResponse>{
            override fun onResponse(call: Call<FoodRecommendResponse>, response: Response<FoodRecommendResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _listFood.value = response.body() as List<FoodRecommendResponse>
                }
                else{
                    Log.e(tag, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FoodRecommendResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(tag, "onFailure: ${t.message}")
            }
        })
    }
}