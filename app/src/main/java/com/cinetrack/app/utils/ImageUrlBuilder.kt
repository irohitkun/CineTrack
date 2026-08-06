package com.cinetrack.app.utils

import com.cinetrack.app.BuildConfig

object ImageUrlBuilder {
    enum class Size(val path: String) {
        POSTER_W185("w185"),
        POSTER_W342("w342"),
        POSTER_W500("w500"),
        BACKDROP_W780("w780"),
        BACKDROP_W1280("w1280"),
        PROFILE_W185("w185"),
        ORIGINAL("original")
    }

    fun build(path: String?, size: Size = Size.POSTER_W342): String? {
        if (path.isNullOrBlank()) return null
        val normalized = if (path.startsWith("/")) path else "/$path"
        return "${BuildConfig.TMDB_IMAGE_BASE_URL}${size.path}$normalized"
    }
}
