package com.cinetrack.app.domain.model

/**
 * User-specific tracking data. Display fields (title/poster) are a thin offline cache
 * for Library cards — not a TMDB catalog mirror.
 */
data class UserMedia(
    val tmdbId: Int,
    val mediaType: MediaType,
    val status: WatchStatus,
    val title: String,
    val posterPath: String?,
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
    val dateAdded: Long = System.currentTimeMillis(),
    val dateFinished: Long? = null
)
