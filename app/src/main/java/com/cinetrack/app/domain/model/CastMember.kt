package com.cinetrack.app.domain.model

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?,
    val order: Int
)
