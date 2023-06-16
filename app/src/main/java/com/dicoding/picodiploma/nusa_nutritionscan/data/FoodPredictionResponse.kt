package com.dicoding.picodiploma.nusa_nutritionscan.model

data class FoodPredictionResponse(
	val msg: String? = null,
	val code: Int? = null,
	val data: Data? = null
)

data class Data(
	val id: String? = null,
	val name: String? = null,
	val calories: Int? = null,
	val karbohidrat: Int? = null,
	val lemak: Int? = null,
	val mineral: Int? = null,
	val protein: Int? = null,
	val vitamin: Int? = null,
	val img: String? = null
)

