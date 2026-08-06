package com.cinetrack.app.domain.model

data class TvSeason(
    val id: Int,
    val name: String,
    val seasonNumber: Int,
    val episodeCount: Int,
    val airDate: String?,
    val overview: String?,
    val posterPath: String?
)
