@file:OptIn(ExperimentalCoroutinesApi::class)

package com.aashika.assignment2.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import com.aashika.assignment2.MainDispatcherRule
import com.aashika.assignment2.data.ApiResult
import com.aashika.assignment2.data.ErrorType
import com.aashika.assignment2.data.model.Movie
import com.aashika.assignment2.data.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieRepository: MovieRepository = mockk()

    private val sampleMovies = listOf(
        Movie(
            title = "The Godfather",
            director = "Francis Ford Coppola",
            genre = "Crime Drama",
            releaseYear = 1972,
            description = "The Corleone family saga."
        )
    )

    private fun createViewModel(keypass: String = "movies") = DashboardViewModel(
        savedStateHandle = SavedStateHandle(mapOf("keypass" to keypass)),
        movieRepository = movieRepository
    )

    @Test
    fun `loads movies on init using keypass from saved state`() = runTest {
        coEvery { movieRepository.getMovies("movies") } returns ApiResult.Success(sampleMovies)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(sampleMovies, viewModel.uiState.value.movies)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `network failure surfaces Network error and empty list`() = runTest {
        coEvery { movieRepository.getMovies(any()) } returns ApiResult.Error(ErrorType.NETWORK)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(DashboardError.Network, viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.movies.isEmpty())
    }

    @Test
    fun `loadMovies retries after a failure`() = runTest {
        coEvery { movieRepository.getMovies("movies") } returnsMany listOf(
            ApiResult.Error(ErrorType.TIMEOUT),
            ApiResult.Success(sampleMovies)
        )

        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(DashboardError.Timeout, viewModel.uiState.value.error)

        viewModel.loadMovies()
        advanceUntilIdle()

        assertEquals(sampleMovies, viewModel.uiState.value.movies)
        assertNull(viewModel.uiState.value.error)
    }
}
