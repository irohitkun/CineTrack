package com.cinetrack.app.domain.model

data class LibraryStats(
    val moviesCompleted: Int = 0,
    val tvCompleted: Int = 0,
    val currentlyWatching: Int = 0,
    val planToWatch: Int = 0,
    val favoritesCount: Int = 0,
    val averagePersonalRating: Double? = null,
    val estimatedHoursWatched: Double = 0.0,
    val mostWatchedGenreId: Int? = null,
    val mostWatchedGenreName: String? = null
)
