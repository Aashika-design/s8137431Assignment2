package com.aashika.assignment2.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponseDto(
    @SerializedName("keypass") val keypass: String
)
