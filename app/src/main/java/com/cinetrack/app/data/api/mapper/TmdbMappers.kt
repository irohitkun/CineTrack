package com.cinetrack.app.data.api.mapper

import com.cinetrack.app.data.api.dto.TmdbCastDto
import com.cinetrack.app.data.api.dto.TmdbCompanyDto
import com.cinetrack.app.data.api.dto.TmdbGenreDto
import com.cinetrack.app.data.api.dto.TmdbMediaDto
import com.cinetrack.app.data.api.dto.TmdbMovieDetailsDto
import com.cinetrack.app.data.api.dto.TmdbSeasonDto
import com.cinetrack.app.data.api.dto.TmdbTvDetailsDto
import com.cinetrack.app.domain.model.CastMember
import com.cinetrack.app.domain.model.Genre
import com.cinetrack.app.domain.model.MediaDetails
import com.cinetrack.app.domain.model.MediaItem
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.domain.model.ProductionCompany
import com.cinetrack.app.domain.model.TvSeason

fun TmdbMediaDto.toDomain(fallbackType: MediaType? = null): MediaItem? {
    val type = when {
        mediaType != null -> MediaType.fromApi(mediaType)
        fallbackType != null -> fallbackType
        title != null -> MediaType.MOVIE
        name != null -> MediaType.TV
        else -> return null
    }
    val resolvedTitle = title ?: name ?: return null
    return MediaItem(
        id = id,
        mediaType = type,
        title = resolvedTitle,
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate ?: firstAirDate,
        voteAverage = voteAverage,
        popularity = popularity,
        genreIds = genreIds.orEmpty()
    )
}

fun List<TmdbMediaDto>.toDomainList(fallbackType: MediaType? = null): List<MediaItem> =
    mapNotNull { it.toDomain(fallbackType) }

fun TmdbGenreDto.toDomain() = Genre(id = id, name = name)

fun TmdbCompanyDto.toDomain() = ProductionCompany(
    id = id,
    name = name,
    logoPath = logoPath,
    originCountry = originCountry
)

fun TmdbCastDto.toDomain() = CastMember(
    id = id,
    name = name,
    character = character.orEmpty(),
    profilePath = profilePath,
    order = order
)

fun TmdbSeasonDto.toDomain() = TvSeason(
    id = id,
    name = name,
    seasonNumber = seasonNumber,
    episodeCount = episodeCount,
    airDate = airDate,
    overview = overview,
    posterPath = posterPath
)

fun TmdbMovieDetailsDto.toDomain() = MediaDetails(
    id = id,
    mediaType = MediaType.MOVIE,
    title = title,
    originalTitle = originalTitle,
    tagline = tagline?.takeIf { it.isNotBlank() },
    overview = overview.orEmpty(),
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    runtimeMinutes = runtime,
    voteAverage = voteAverage,
    voteCount = voteCount,
    popularity = popularity,
    genres = genres.map { it.toDomain() },
    productionCompanies = productionCompanies.map { it.toDomain() },
    cast = credits?.cast.orEmpty().map { it.toDomain() }.sortedBy { it.order },
    status = status
)

fun TmdbTvDetailsDto.toDomain() = MediaDetails(
    id = id,
    mediaType = MediaType.TV,
    title = name,
    originalTitle = originalName,
    tagline = tagline?.takeIf { it.isNotBlank() },
    overview = overview.orEmpty(),
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = firstAirDate,
    runtimeMinutes = episodeRunTime?.firstOrNull(),
    voteAverage = voteAverage,
    voteCount = voteCount,
    popularity = popularity,
    genres = genres.map { it.toDomain() },
    productionCompanies = productionCompanies.map { it.toDomain() },
    cast = credits?.cast.orEmpty().map { it.toDomain() }.sortedBy { it.order },
    seasons = seasons
        .filter { it.seasonNumber > 0 }
        .map { it.toDomain() },
    numberOfSeasons = numberOfSeasons,
    numberOfEpisodes = numberOfEpisodes,
    status = status
)
