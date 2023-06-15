package com.dicoding.picodiploma.nusa_nutritionscan.data

data class LoginResponse(
	val msg: String? = null,
	val code: Int? = null,
	val data: Data? = null
)

data class Data(
	val sex: String? = null,
	val weight: Int? = null,
	val token: String? = null,
	val refresh_token: String? = null,
	val eat_per_day: Int? = null,
	val calories_target: Int? = null,
	val has_been_updated: Boolean? = null,
	val name: String? = null,
	val id: String? = null,
	val expires_in: String? = null,
	val age: Int? = null,
	val email: String? = null,
	val height: Int? = null
)
