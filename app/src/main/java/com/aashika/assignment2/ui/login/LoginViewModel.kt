package com.aashika.assignment2.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aashika.assignment2.data.ApiResult
import com.aashika.assignment2.data.ErrorType
import com.aashika.assignment2.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigateToDashboard = MutableSharedFlow<String>()
    val navigateToDashboard: SharedFlow<String> = _navigateToDashboard.asSharedFlow()

    fun clearError() {
        if (_uiState.value.error != null) {
            _uiState.update { it.copy(error = null) }
        }
    }

    fun onLoginClicked(username: String, password: String) {
        when {
            username.isBlank() -> _uiState.update { it.copy(error = LoginError.EmptyUsername) }
            password.isBlank() -> _uiState.update { it.copy(error = LoginError.EmptyPassword) }
            else -> login(username.trim(), password)
        }
    }

    private fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.login(username, password)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _navigateToDashboard.emit(result.data)
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.type.toLoginError()) }
                }
            }
        }
    }

    private fun ErrorType.toLoginError(): LoginError = when (this) {
        ErrorType.UNAUTHORIZED, ErrorType.SERVER -> LoginError.InvalidCredentials
        ErrorType.NETWORK -> LoginError.Network
        ErrorType.TIMEOUT -> LoginError.Timeout
        ErrorType.UNKNOWN -> LoginError.Unknown
    }
}
