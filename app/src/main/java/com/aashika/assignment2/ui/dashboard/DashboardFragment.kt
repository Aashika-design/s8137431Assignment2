package com.aashika.assignment2.ui.dashboard

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
import com.aashika.assignment2.data.model.Movie
import com.aashika.assignment2.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private val movieAdapter = MovieAdapter(onMovieClick = ::onMovieClick)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerMovies.adapter = movieAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> renderState(state) }
            }
        }
    }

    private fun renderState(state: DashboardUiState) {
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        movieAdapter.submitList(state.movies)

        val message = when {
            state.error != null -> getString(state.error.toStringRes())
            !state.isLoading && state.movies.isEmpty() -> getString(R.string.dashboard_empty)
            else -> null
        }
        binding.textEmptyOrError.text = message
        binding.textEmptyOrError.visibility = if (message != null) View.VISIBLE else View.GONE
        binding.recyclerMovies.visibility =
            if (message == null && !state.isLoading) View.VISIBLE else View.GONE
    }

    private fun DashboardError.toStringRes(): Int = when (this) {
        DashboardError.Network -> R.string.error_network
        DashboardError.Timeout -> R.string.error_timeout
        DashboardError.Unknown -> R.string.dashboard_error
    }

    private fun onMovieClick(movie: Movie) {
        findNavController().navigate(
            DashboardFragmentDirections.actionDashboardToDetails(
                title = movie.title,
                director = movie.director,
                genre = movie.genre,
                releaseYear = movie.releaseYear,
                description = movie.description
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerMovies.adapter = null
        _binding = null
    }
}
