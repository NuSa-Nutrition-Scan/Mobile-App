package com.dicoding.picodiploma.nusa_nutritionscan.data

data class LogInResponse(
	val msg: String? = null,
	val code: Int? = null,
	val data: Data? = null
)

data class Data(
	val refreshToken: String? = null,
	val name: String? = null,
	val id: String? = null,
	val expiresIn: String? = null,
	val email: String? = null,
	val token: String? = null
)

