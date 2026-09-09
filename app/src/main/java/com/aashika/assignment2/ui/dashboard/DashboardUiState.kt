package com.aashika.assignment2.ui.dashboard

import com.aashika.assignment2.data.model.Movie

data class DashboardUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: DashboardError? = null
)

sealed class DashboardError {
    object Network : DashboardError()
    object Timeout : DashboardError()
    object Unknown : DashboardError()
}
