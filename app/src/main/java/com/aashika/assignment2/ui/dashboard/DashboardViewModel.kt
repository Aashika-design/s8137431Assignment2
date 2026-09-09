package com.aashika.assignment2.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aashika.assignment2.data.ApiResult
import com.aashika.assignment2.data.ErrorType
import com.aashika.assignment2.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val keypass: String =
        checkNotNull(savedStateHandle["keypass"]) { "keypass navigation argument is required" }

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = movieRepository.getMovies(keypass)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(isLoading = false, movies = result.data)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.type.toDashboardError())
                }
            }
        }
    }

    private fun ErrorType.toDashboardError(): DashboardError = when (this) {
        ErrorType.NETWORK -> DashboardError.Network
        ErrorType.TIMEOUT -> DashboardError.Timeout
        ErrorType.UNAUTHORIZED, ErrorType.SERVER, ErrorType.UNKNOWN -> DashboardError.Unknown
    }
}
