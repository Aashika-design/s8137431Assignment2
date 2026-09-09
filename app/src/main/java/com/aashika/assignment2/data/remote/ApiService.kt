package com.aashika.assignment2.data.remote

import com.aashika.assignment2.data.remote.dto.DashboardResponseDto
import com.aashika.assignment2.data.remote.dto.LoginRequestDto
import com.aashika.assignment2.data.remote.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("sydney/auth")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @GET("dashboard/{keypass}")
    suspend fun getDashboard(@Path("keypass") keypass: String): DashboardResponseDto
}
