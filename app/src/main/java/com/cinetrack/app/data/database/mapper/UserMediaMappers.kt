package com.cinetrack.app.data.database.mapper

import com.cinetrack.app.data.database.entity.UserMediaEntity
import com.cinetrack.app.domain.model.UserMedia

fun UserMediaEntity.toDomain() = UserMedia(
    tmdbId = tmdbId,
    mediaType = mediaType,
    status = status,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    runtimeMinutes = runtimeMinutes,
    genreIds = genreIds
        .split(",")
        .mapNotNull { it.trim().toIntOrNull() },
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

fun UserMedia.toEntity() = UserMediaEntity(
    tmdbId = tmdbId,
    mediaType = mediaType,
    status = status,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    runtimeMinutes = runtimeMinutes,
    genreIds = genreIds.joinToString(","),
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
