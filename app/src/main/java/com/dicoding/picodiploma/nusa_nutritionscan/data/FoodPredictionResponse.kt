package com.dicoding.picodiploma.nusa_nutritionscan.model

data class FoodPredictionResponse(
	val msg: String? = null,
	val code: Int? = null,
	val data: Data_food? = null
)

data class Data_food(
	val finalResult: String? = null,
	val otherOptions: List<String?>? = null
)

