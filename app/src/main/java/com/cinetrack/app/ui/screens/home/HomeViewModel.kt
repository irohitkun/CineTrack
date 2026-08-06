package com.cinetrack.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinetrack.app.data.repository.TmdbRepository
import com.cinetrack.app.domain.model.MediaItem
import com.cinetrack.app.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeSection(
    val title: String,
    val items: List<MediaItem> = emptyList()
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val sections: List<HomeSection> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tmdbRepository: TmdbRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val sections = fetchSections()
                val hasAny = sections.any { it.items.isNotEmpty() }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        sections = sections,
                        errorMessage = if (!hasAny) {
                            "No content loaded. Check your network and TMDB API key."
                        } else {
                            null
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load home"
                    )
                }
            }
        }
    }

    private suspend fun fetchSections(): List<HomeSection> = coroutineScope {
        val trendingMovies = async { tmdbRepository.getTrendingMovies() }
        val trendingTv = async { tmdbRepository.getTrendingTv() }
        val nowPlaying = async { tmdbRepository.getNowPlayingMovies() }
        val upcoming = async { tmdbRepository.getUpcomingMovies() }
        val topRated = async { tmdbRepository.getTopRatedMovies() }
        val popularMovies = async { tmdbRepository.getPopularMovies() }
        val popularTv = async { tmdbRepository.getPopularTv() }

        listOf(
            HomeSection("Trending Movies", trendingMovies.await().itemsOrEmpty()),
            HomeSection("Trending TV", trendingTv.await().itemsOrEmpty()),
            HomeSection("Now Playing", nowPlaying.await().itemsOrEmpty()),
            HomeSection("Upcoming Movies", upcoming.await().itemsOrEmpty()),
            HomeSection("Top Rated Movies", topRated.await().itemsOrEmpty()),
            HomeSection("Popular Movies", popularMovies.await().itemsOrEmpty()),
            HomeSection("Popular TV Shows", popularTv.await().itemsOrEmpty())
        )
    }

    private fun Result<List<MediaItem>>.itemsOrEmpty(): List<MediaItem> =
        (this as? Result.Success)?.data.orEmpty()
}
