package com.aashika.assignment2.data.repository

import com.aashika.assignment2.data.ApiResult
import com.aashika.assignment2.data.model.Movie
import com.aashika.assignment2.data.model.toDomain
import com.aashika.assignment2.data.remote.ApiService
import com.aashika.assignment2.data.safeApiCall
import javax.inject.Inject

interface MovieRepository {
    suspend fun getMovies(keypass: String): ApiResult<List<Movie>>
}

class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MovieRepository {

    override suspend fun getMovies(keypass: String): ApiResult<List<Movie>> =
        safeApiCall {
            apiService.getDashboard(keypass).entities.map { it.toDomain() }
        }
}
