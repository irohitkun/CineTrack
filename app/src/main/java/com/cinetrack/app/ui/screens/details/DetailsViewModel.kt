package com.cinetrack.app.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinetrack.app.data.repository.LibraryRepository
import com.cinetrack.app.data.repository.TmdbRepository
import com.cinetrack.app.domain.model.MediaDetails
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.domain.model.UserMedia
import com.cinetrack.app.domain.model.WatchStatus
import com.cinetrack.app.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailsUiState(
    val isLoading: Boolean = true,
    val details: MediaDetails? = null,
    val userMedia: UserMedia? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tmdbRepository: TmdbRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val mediaType: MediaType = MediaType.fromApi(savedStateHandle["mediaType"])
    private val mediaId: Int = checkNotNull(savedStateHandle["mediaId"])

    private val _remote = MutableStateFlow(DetailsRemoteState())
    val uiState: StateFlow<DetailsUiState> = combine(
        _remote,
        libraryRepository.observeItem(mediaId, mediaType)
    ) { remote, userMedia ->
        DetailsUiState(
            isLoading = remote.isLoading,
            details = remote.details,
            userMedia = userMedia,
            errorMessage = remote.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailsUiState()
    )

    init {
        loadDetails()
    }

    fun loadDetails() {
        viewModelScope.launch {
            _remote.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = tmdbRepository.getDetails(mediaType, mediaId)) {
                is Result.Success -> _remote.update {
                    it.copy(isLoading = false, details = result.data, errorMessage = null)
                }
                is Result.Error -> _remote.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun setStatus(status: WatchStatus) {
        viewModelScope.launch {
            val details = _remote.value.details ?: return@launch
            val existing = libraryRepository.getItem(mediaId, mediaType)
            if (existing == null) {
                libraryRepository.upsert(details.toUserMedia(status = status))
            } else {
                libraryRepository.updateStatus(mediaId, mediaType, status)
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val details = _remote.value.details ?: return@launch
            val existing = libraryRepository.getItem(mediaId, mediaType)
            if (existing == null) {
                libraryRepository.upsert(
                    details.toUserMedia(
                        status = WatchStatus.PLAN_TO_WATCH,
                        isFavorite = true
                    )
                )
            } else {
                libraryRepository.setFavorite(mediaId, mediaType, !existing.isFavorite)
            }
        }
    }

    fun setPersonalRating(rating: Float?) {
        viewModelScope.launch {
            ensureInLibrary()
            libraryRepository.setPersonalRating(mediaId, mediaType, rating)
        }
    }

    fun setNotes(notes: String) {
        viewModelScope.launch {
            ensureInLibrary()
            libraryRepository.setNotes(mediaId, mediaType, notes)
        }
    }

    fun setReview(review: String) {
        viewModelScope.launch {
            ensureInLibrary()
            libraryRepository.setReview(mediaId, mediaType, review)
        }
    }

    fun adjustSeason(delta: Int) {
        viewModelScope.launch {
            val user = ensureInLibrary() ?: return@launch
            val details = _remote.value.details
            val maxSeason = details?.numberOfSeasons ?: details?.seasons?.maxOfOrNull { it.seasonNumber } ?: 99
            val newSeason = (user.currentSeason + delta).coerceIn(1, maxSeason.coerceAtLeast(1))
            libraryRepository.updateTvProgress(mediaId, mediaType, newSeason, user.currentEpisode)
        }
    }

    fun adjustEpisode(delta: Int) {
        viewModelScope.launch {
            val user = ensureInLibrary() ?: return@launch
            val details = _remote.value.details
            val season = details?.seasons?.firstOrNull { it.seasonNumber == user.currentSeason }
            val maxEpisode = season?.episodeCount?.takeIf { it > 0 } ?: 50
            val newEpisode = (user.currentEpisode + delta).coerceIn(1, maxEpisode)
            libraryRepository.updateTvProgress(mediaId, mediaType, user.currentSeason, newEpisode)
        }
    }

    fun markCompleted() {
        setStatus(WatchStatus.COMPLETED)
    }

    fun removeFromLibrary() {
        viewModelScope.launch {
            libraryRepository.remove(mediaId, mediaType)
        }
    }

    fun shareText(): String? {
        val details = _remote.value.details ?: return null
        val typePath = details.mediaType.apiValue
        return "${details.title}\nhttps://www.themoviedb.org/$typePath/${details.id}"
    }

    private suspend fun ensureInLibrary(): UserMedia? {
        val existing = libraryRepository.getItem(mediaId, mediaType)
        if (existing != null) return existing
        val details = _remote.value.details ?: return null
        val created = details.toUserMedia(status = WatchStatus.PLAN_TO_WATCH)
        libraryRepository.upsert(created)
        return created
    }

    private fun MediaDetails.toUserMedia(
        status: WatchStatus,
        isFavorite: Boolean = false
    ) = UserMedia(
        tmdbId = id,
        mediaType = mediaType,
        status = status,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        runtimeMinutes = runtimeMinutes,
        genreIds = genres.map { it.id },
        isFavorite = isFavorite,
        currentSeason = 1,
        currentEpisode = 1,
        totalSeasons = numberOfSeasons,
        totalEpisodes = numberOfEpisodes,
        dateFinished = if (status == WatchStatus.COMPLETED) System.currentTimeMillis() else null
    )

    private data class DetailsRemoteState(
        val isLoading: Boolean = true,
        val details: MediaDetails? = null,
        val errorMessage: String? = null
    )
}
