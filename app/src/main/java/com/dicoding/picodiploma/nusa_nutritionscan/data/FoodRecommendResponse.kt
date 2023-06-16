package com.dicoding.picodiploma.nusa_nutritionscan.Activity

data class FoodRecommendResponse(
	val msg: String? = null,
	val code: Int? = null,
	val data: Data? = null
)

data class Data(
	val recom: List<RecomItem?>? = null,
	val top15: List<Top15Item?>? = null
)

data class RecomItem(
	val breakfast: String? = null,
	val dinner: String? = null,
	val lunch: String? = null
)

data class Top15Item(
	val id: String? = null,
	val name: String? = null,
	val img: String? = null
)

