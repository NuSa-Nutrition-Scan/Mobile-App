package com.dicoding.picodiploma.nusa_nutritionscan.API

data class ImageUploadResponse(
	val msg: String? = null,
	val code: Int? = null,
	val data: Data? = null
)

data class Data(
	val userId: String? = null,
	val imgUrl: String? = null,
	val createdAt: String? = null
)

