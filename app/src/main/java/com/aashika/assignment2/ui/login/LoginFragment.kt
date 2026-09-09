package com.aashika.assignment2.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.aashika.assignment2.R
import com.aashika.assignment2.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val username = binding.editUsername.text?.toString().orEmpty()
            val password = binding.editPassword.text?.toString().orEmpty()
            viewModel.onLoginClicked(username, password)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state -> renderState(state) }
                }
                launch {
                    viewModel.navigateToDashboard.collect { keypass ->
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginToDashboard(keypass)
                        )
                    }
                }
            }
        }
    }

    private fun renderState(state: LoginUiState) {
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !state.isLoading

        val errorText = state.error?.let { getString(it.toStringRes()) }
        binding.textError.text = errorText
        binding.textError.visibility = if (errorText != null) View.VISIBLE else View.GONE
    }

    private fun LoginError.toStringRes(): Int = when (this) {
        LoginError.EmptyUsername -> R.string.error_username_required
        LoginError.EmptyPassword -> R.string.error_password_required
        LoginError.InvalidCredentials -> R.string.error_login_failed
        LoginError.Network -> R.string.error_network
        LoginError.Timeout -> R.string.error_timeout
        LoginError.Unknown -> R.string.error_generic
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
