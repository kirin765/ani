package com.kiwankim.kiwankim.myapplication3.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val code: String = "",
    val data: T? = null,
)
