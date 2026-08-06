package com.cinetrack.app.domain.model

/**
 * Lightweight TMDB list/search result. Never persisted as catalog data.
 */
data class MediaItem(
    val id: Int,
    val mediaType: MediaType,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val popularity: Double,
    val genreIds: List<Int> = emptyList()
) {
    val year: String?
        get() = releaseDate?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) }
}
