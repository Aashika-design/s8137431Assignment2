package com.aashika.assignment2.ui.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: LoginError? = null
)

sealed class LoginError {
    object EmptyUsername : LoginError()
    object EmptyPassword : LoginError()
    object InvalidCredentials : LoginError()
    object Network : LoginError()
    object Timeout : LoginError()
    object Unknown : LoginError()
}
