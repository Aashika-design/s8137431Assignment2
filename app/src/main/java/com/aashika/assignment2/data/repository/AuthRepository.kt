package com.aashika.assignment2.data.repository

import com.aashika.assignment2.data.ApiResult
import com.aashika.assignment2.data.remote.ApiService
import com.aashika.assignment2.data.remote.dto.LoginRequestDto
import com.aashika.assignment2.data.safeApiCall
import javax.inject.Inject

interface AuthRepository {
    suspend fun login(username: String, password: String): ApiResult<String>
}

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(username: String, password: String): ApiResult<String> =
        safeApiCall {
            apiService.login(LoginRequestDto(username = username, password = password)).keypass
        }
}
