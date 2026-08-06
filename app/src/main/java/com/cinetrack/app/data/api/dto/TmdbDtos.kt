package com.cinetrack.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TmdbPagedResponseDto(
    val page: Int = 1,
    val results: List<TmdbMediaDto> = emptyList(),
    @Json(name = "total_pages") val totalPages: Int = 0,
    @Json(name = "total_results") val totalResults: Int = 0
)

@JsonClass(generateAdapter = true)
data class TmdbMediaDto(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    val overview: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "vote_average") val voteAverage: Double = 0.0,
    val popularity: Double = 0.0,
    @Json(name = "genre_ids") val genreIds: List<Int>? = null,
    @Json(name = "media_type") val mediaType: String? = null
)

@JsonClass(generateAdapter = true)
data class TmdbGenreDto(
    val id: Int,
    val name: String
)

@JsonClass(generateAdapter = true)
data class TmdbCompanyDto(
    val id: Int,
    val name: String,
    @Json(name = "logo_path") val logoPath: String? = null,
    @Json(name = "origin_country") val originCountry: String? = null
)

@JsonClass(generateAdapter = true)
data class TmdbCastDto(
    val id: Int,
    val name: String,
    val character: String? = null,
    @Json(name = "profile_path") val profilePath: String? = null,
    val order: Int = 0
)

@JsonClass(generateAdapter = true)
data class TmdbCreditsDto(
    val cast: List<TmdbCastDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class TmdbSeasonDto(
    val id: Int,
    val name: String,
    @Json(name = "season_number") val seasonNumber: Int = 0,
    @Json(name = "episode_count") val episodeCount: Int = 0,
    @Json(name = "air_date") val airDate: String? = null,
    val overview: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null
)

@JsonClass(generateAdapter = true)
data class TmdbMovieDetailsDto(
    val id: Int,
    val title: String,
    @Json(name = "original_title") val originalTitle: String? = null,
    val tagline: String? = null,
    val overview: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    val runtime: Int? = null,
    @Json(name = "vote_average") val voteAverage: Double = 0.0,
    @Json(name = "vote_count") val voteCount: Int = 0,
    val popularity: Double = 0.0,
    val genres: List<TmdbGenreDto> = emptyList(),
    @Json(name = "production_companies") val productionCompanies: List<TmdbCompanyDto> = emptyList(),
    val credits: TmdbCreditsDto? = null,
    val status: String? = null
)

@JsonClass(generateAdapter = true)
data class TmdbTvDetailsDto(
    val id: Int,
    val name: String,
    @Json(name = "original_name") val originalName: String? = null,
    val tagline: String? = null,
    val overview: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "episode_run_time") val episodeRunTime: List<Int>? = null,
    @Json(name = "vote_average") val voteAverage: Double = 0.0,
    @Json(name = "vote_count") val voteCount: Int = 0,
    val popularity: Double = 0.0,
    val genres: List<TmdbGenreDto> = emptyList(),
    @Json(name = "production_companies") val productionCompanies: List<TmdbCompanyDto> = emptyList(),
    val credits: TmdbCreditsDto? = null,
    val seasons: List<TmdbSeasonDto> = emptyList(),
    @Json(name = "number_of_seasons") val numberOfSeasons: Int? = null,
    @Json(name = "number_of_episodes") val numberOfEpisodes: Int? = null,
    val status: String? = null
)

@JsonClass(generateAdapter = true)
data class TmdbGenreListDto(
    val genres: List<TmdbGenreDto> = emptyList()
)
