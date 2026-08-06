package com.cinetrack.app.domain.model

/**
 * Full TMDB details payload for the Details screen. Online-only; not stored in Room.
 */
data class MediaDetails(
    val id: Int,
    val mediaType: MediaType,
    val title: String,
    val originalTitle: String?,
    val tagline: String?,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val runtimeMinutes: Int?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val genres: List<Genre>,
    val productionCompanies: List<ProductionCompany>,
    val cast: List<CastMember>,
    val seasons: List<TvSeason> = emptyList(),
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,
    val status: String? = null
) {
    val year: String?
        get() = releaseDate?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) }
}
