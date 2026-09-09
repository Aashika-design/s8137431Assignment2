package com.aashika.assignment2.data

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val type: ErrorType, val message: String? = null) : ApiResult<Nothing>()
}

enum class ErrorType {
    NETWORK,
    TIMEOUT,
    UNAUTHORIZED,
    SERVER,
    UNKNOWN
}

suspend fun <T> safeApiCall(block: suspend () -> T): ApiResult<T> = try {
    ApiResult.Success(block())
} catch (e: SocketTimeoutException) {
    ApiResult.Error(ErrorType.TIMEOUT, e.message)
} catch (e: IOException) {
    ApiResult.Error(ErrorType.NETWORK, e.message)
} catch (e: HttpException) {
    val type = if (e.code() == 401 || e.code() == 403) ErrorType.UNAUTHORIZED else ErrorType.SERVER
    ApiResult.Error(type, e.message())
} catch (e: Exception) {
    ApiResult.Error(ErrorType.UNKNOWN, e.message)
}
