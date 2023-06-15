package com.dicoding.picodiploma.nusa_nutritionscan.API

import com.dicoding.picodiploma.nusa_nutritionscan.Activity.FoodRecommendResponse
import com.dicoding.picodiploma.nusa_nutritionscan.data.LoginResponse
import com.dicoding.picodiploma.nusa_nutritionscan.data.SignUpResponse
import com.dicoding.picodiploma.nusa_nutritionscan.model.FoodPredictionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("auth/signin")
    fun Login(@Body body: RequestBody): retrofit2.Call<LoginResponse>

    @FormUrlEncoded
    @PATCH("settings/profile/update")
    fun UpdateProfile(
        @Header("Authorization") bearer: String?,
        @Field("name") name: String?,
        @Field("weight") weight: Int?,
        @Field("height") height: Int?,
        @Field("sex") sex: String?,
        @Field("calories_target") calories_target: Int?,
        @Field("age") age: Int?,
        @Field("eat_per_day") eat_per_day: Int?,
        @Field("refresh_token") refresh_token: String?
    ): retrofit2.Call<LoginResponse>

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
    ): retrofit2.Call<LoginResponse>

    @Multipart
    @POST("nutrition/photo/predict_food_secure")
    fun predictionFood(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part
    ): retrofit2.Call<FoodPredictionResponse>

    @Multipart
    @POST("nutrition/photo")
    fun uploadImage(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part
    ): retrofit2.Call<ImageUploadResponse>

    @Headers("Content-Type: application/json")
    @GET("/nutrition/recommendation")
    fun getRecommend(
        @Header("Authorization") bearer: String?
    ): Call<FoodRecommendResponse>
}