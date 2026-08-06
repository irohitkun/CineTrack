package com.cinetrack.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinetrack.app.data.repository.TmdbRepository
import com.cinetrack.app.domain.model.MediaItem
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchFilter {
    ALL,
    MOVIES,
    TV
}

data class SearchUiState(
    val query: String = "",
    val filter: SearchFilter = SearchFilter.ALL,
    val results: List<MediaItem> = emptyList(),
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val tmdbRepository: TmdbRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, errorMessage = null) }
        debounceSearch(query)
    }

    fun onFilterChange(filter: SearchFilter) {
        _uiState.update { it.copy(filter = filter) }
        val query = _uiState.value.query
        if (query.isNotBlank()) {
            debounceSearch(query, immediate = true)
        }
    }

    fun retry() {
        debounceSearch(_uiState.value.query, immediate = true)
    }

    fun clearQuery() {
        searchJob?.cancel()
        _uiState.value = SearchUiState(filter = _uiState.value.filter)
    }

    private fun debounceSearch(query: String, immediate: Boolean = false) {
        searchJob?.cancel()
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            _uiState.update {
                it.copy(
                    results = emptyList(),
                    isSearching = false,
                    hasSearched = false,
                    errorMessage = null
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            if (!immediate) delay(DEBOUNCE_MS)
            _uiState.update { it.copy(isSearching = true, errorMessage = null) }

            val result = when (_uiState.value.filter) {
                SearchFilter.ALL -> tmdbRepository.searchMulti(trimmed)
                SearchFilter.MOVIES -> tmdbRepository.searchMovies(trimmed)
                SearchFilter.TV -> tmdbRepository.searchTv(trimmed)
            }

            when (result) {
                is Result.Success -> {
                    val filtered = result.data.filter {
                        it.mediaType == MediaType.MOVIE || it.mediaType == MediaType.TV
                    }
                    _uiState.update {
                        it.copy(
                            results = filtered,
                            isSearching = false,
                            hasSearched = true,
                            errorMessage = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            results = emptyList(),
                            isSearching = false,
                            hasSearched = true,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val DEBOUNCE_MS = 300L
    }
}
