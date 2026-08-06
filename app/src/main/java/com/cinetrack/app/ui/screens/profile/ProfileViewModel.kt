package com.cinetrack.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinetrack.app.data.preferences.SettingsPreferences
import com.cinetrack.app.data.repository.LibraryRepository
import com.cinetrack.app.data.repository.TmdbRepository
import com.cinetrack.app.domain.model.LibraryStats
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.domain.model.UserMedia
import com.cinetrack.app.domain.model.WatchStatus
import com.cinetrack.app.utils.Result
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val stats: LibraryStats = LibraryStats(),
    val darkTheme: Boolean = true,
    val message: String? = null,
    val isBusy: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val tmdbRepository: TmdbRepository,
    private val settingsPreferences: SettingsPreferences,
    private val moshi: Moshi
) : ViewModel() {

    private val message = MutableStateFlow<String?>(null)
    private val busy = MutableStateFlow(false)
    private val genreNames = MutableStateFlow<Map<Int, String>>(emptyMap())

    val uiState: StateFlow<ProfileUiState> = combine(
        libraryRepository.observeAll(),
        settingsPreferences.darkTheme,
        genreNames,
        message,
        busy
    ) { library, dark, genres, msg, isBusy ->
        ProfileUiState(
            stats = computeStats(library, genres),
            darkTheme = dark,
            message = msg,
            isBusy = isBusy
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    init {
        viewModelScope.launch { loadGenres() }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { settingsPreferences.setDarkTheme(enabled) }
    }

    fun clearLibrary() {
        viewModelScope.launch {
            busy.value = true
            libraryRepository.clearLibrary()
            message.value = "Library cleared"
            busy.value = false
        }
    }

    fun exportLibraryAsync(onReady: (String?) -> Unit) {
        viewModelScope.launch {
            busy.value = true
            val json = try {
                val items = libraryRepository.getAll()
                val type = Types.newParameterizedType(List::class.java, UserMediaExportDto::class.java)
                moshi.adapter<List<UserMediaExportDto>>(type).toJson(items.map { it.toExportDto() })
            } catch (e: Exception) {
                message.value = e.message
                null
            }
            busy.value = false
            onReady(json)
        }
    }

    fun importLibrary(json: String) {
        viewModelScope.launch {
            busy.value = true
            try {
                val type = Types.newParameterizedType(List::class.java, UserMediaExportDto::class.java)
                val items = moshi.adapter<List<UserMediaExportDto>>(type)
                    .fromJson(json)
                    .orEmpty()
                    .map { it.toDomain() }
                libraryRepository.importAll(items)
                message.value = "Imported ${items.size} titles"
            } catch (e: Exception) {
                message.value = "Import failed: ${e.message}"
            }
            busy.value = false
        }
    }

    fun clearMessage() {
        message.value = null
    }

    private suspend fun loadGenres() {
        val map = mutableMapOf<Int, String>()
        when (val movie = tmdbRepository.getMovieGenres()) {
            is Result.Success -> movie.data.forEach { map[it.id] = it.name }
            is Result.Error -> Unit
        }
        when (val tv = tmdbRepository.getTvGenres()) {
            is Result.Success -> tv.data.forEach { map[it.id] = it.name }
            is Result.Error -> Unit
        }
        genreNames.value = map
    }

    private fun computeStats(library: List<UserMedia>, genres: Map<Int, String>): LibraryStats {
        val moviesCompleted = library.count {
            it.status == WatchStatus.COMPLETED && it.mediaType == MediaType.MOVIE
        }
        val tvCompleted = library.count {
            it.status == WatchStatus.COMPLETED && it.mediaType == MediaType.TV
        }
        val ratings = library.mapNotNull { it.personalRating }
        val minutes = library
            .filter { it.status == WatchStatus.COMPLETED || it.status == WatchStatus.WATCHING }
            .sumOf { (it.runtimeMinutes ?: defaultRuntime(it.mediaType)).toLong() }

        val genreCounts = mutableMapOf<Int, Int>()
        library.forEach { item ->
            item.genreIds.forEach { id ->
                genreCounts[id] = (genreCounts[id] ?: 0) + 1
            }
        }
        val topGenreId = genreCounts.maxByOrNull { it.value }?.key

        return LibraryStats(
            moviesCompleted = moviesCompleted,
            tvCompleted = tvCompleted,
            currentlyWatching = library.count { it.status == WatchStatus.WATCHING },
            planToWatch = library.count { it.status == WatchStatus.PLAN_TO_WATCH },
            favoritesCount = library.count { it.isFavorite },
            averagePersonalRating = if (ratings.isNotEmpty()) ratings.average() else null,
            estimatedHoursWatched = minutes / 60.0,
            mostWatchedGenreId = topGenreId,
            mostWatchedGenreName = topGenreId?.let { genres[it] }
        )
    }

    private fun defaultRuntime(type: MediaType): Int = when (type) {
        MediaType.MOVIE -> 120
        MediaType.TV -> 45
    }
}

@JsonClass(generateAdapter = true)
data class UserMediaExportDto(
    val tmdbId: Int,
    val mediaType: String,
    val status: String,
    val title: String,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val runtimeMinutes: Int? = null,
    val genreIds: List<Int> = emptyList(),
    val personalRating: Float? = null,
    val isFavorite: Boolean = false,
    val notes: String = "",
    val review: String = "",
    val currentSeason: Int = 1,
    val currentEpisode: Int = 1,
    val totalSeasons: Int? = null,
    val totalEpisodes: Int? = null,
    val dateAdded: Long = 0L,
    val dateFinished: Long? = null
)

private fun UserMedia.toExportDto() = UserMediaExportDto(
    tmdbId = tmdbId,
    mediaType = mediaType.name,
    status = status.name,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    runtimeMinutes = runtimeMinutes,
    genreIds = genreIds,
    personalRating = personalRating,
    isFavorite = isFavorite,
    notes = notes,
    review = review,
    currentSeason = currentSeason,
    currentEpisode = currentEpisode,
    totalSeasons = totalSeasons,
    totalEpisodes = totalEpisodes,
    dateAdded = dateAdded,
    dateFinished = dateFinished
)

private fun UserMediaExportDto.toDomain() = UserMedia(
    tmdbId = tmdbId,
    mediaType = MediaType.valueOf(mediaType),
    status = WatchStatus.valueOf(status),
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    runtimeMinutes = runtimeMinutes,
    genreIds = genreIds,
    personalRating = personalRating,
    isFavorite = isFavorite,
    notes = notes,
    review = review,
    currentSeason = currentSeason,
    currentEpisode = currentEpisode,
    totalSeasons = totalSeasons,
    totalEpisodes = totalEpisodes,
    dateAdded = dateAdded,
    dateFinished = dateFinished
)
