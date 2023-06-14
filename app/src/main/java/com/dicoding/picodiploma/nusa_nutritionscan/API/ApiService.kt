package com.dicoding.picodiploma.nusa_nutritionscan.API

import com.dicoding.picodiploma.nusa_nutritionscan.data.LogInResponse
import com.dicoding.picodiploma.nusa_nutritionscan.data.SignUpResponse
import com.dicoding.picodiploma.nusa_nutritionscan.model.FoodPredictionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("auth/signin")
    fun Login(@Body body: RequestBody): retrofit2.Call<LogInResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/signup")
    fun Register(
        @Body body: RequestBody
    ): retrofit2.Call<SignUpResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/signout")
    fun Logout(
        @Header("Authorization") bearer: String?
    ): retrofit2.Call<SignUpResponse>

    @Headers("Content-Type: application/json")
    @PUT("auth/refresh")
    fun Refresh(
        @Body body: RequestBody
    ): retrofit2.Call<LogInResponse>

    @Multipart
    @POST("nutrition/photo/predict_food_secure")
    fun uploadImage(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part
    ): retrofit2.Call<FoodPredictionResponse>
}