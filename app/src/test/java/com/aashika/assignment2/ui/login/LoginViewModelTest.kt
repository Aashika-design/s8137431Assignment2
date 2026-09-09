@file:OptIn(ExperimentalCoroutinesApi::class)

package com.aashika.assignment2.ui.login

import com.aashika.assignment2.MainDispatcherRule
import com.aashika.assignment2.data.ApiResult
import com.aashika.assignment2.data.ErrorType
import com.aashika.assignment2.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository: AuthRepository = mockk()

    @Test
    fun `blank username sets EmptyUsername error without calling repository`() = runTest {
        val viewModel = LoginViewModel(authRepository)

        viewModel.onLoginClicked(username = "", password = "Aashika")

        assertEquals(LoginError.EmptyUsername, viewModel.uiState.value.error)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `blank password sets EmptyPassword error without calling repository`() = runTest {
        val viewModel = LoginViewModel(authRepository)

        viewModel.onLoginClicked(username = "8137431", password = "")

        assertEquals(LoginError.EmptyPassword, viewModel.uiState.value.error)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `successful login clears loading and emits keypass for navigation`() = runTest {
        coEvery { authRepository.login("8137431", "Aashika") } returns ApiResult.Success("movies")
        val viewModel = LoginViewModel(authRepository)

        val emittedKeypasses = mutableListOf<String>()
        val collectJob = launch { viewModel.navigateToDashboard.toList(emittedKeypasses) }

        viewModel.onLoginClicked(username = "8137431", password = "Aashika")
        advanceUntilIdle()

        assertEquals(listOf("movies"), emittedKeypasses)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
        collectJob.cancel()
    }

    @Test
    fun `failed login with unauthorized error surfaces InvalidCredentials`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns
            ApiResult.Error(ErrorType.UNAUTHORIZED)
        val viewModel = LoginViewModel(authRepository)

        viewModel.onLoginClicked(username = "8137431", password = "wrong-password")
        advanceUntilIdle()

        assertEquals(LoginError.InvalidCredentials, viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `failed login with network error surfaces Network`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns ApiResult.Error(ErrorType.NETWORK)
        val viewModel = LoginViewModel(authRepository)

        viewModel.onLoginClicked(username = "8137431", password = "Aashika")
        advanceUntilIdle()

        assertEquals(LoginError.Network, viewModel.uiState.value.error)
    }
}
