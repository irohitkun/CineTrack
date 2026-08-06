package com.cinetrack.app.domain.model

enum class MediaType(val apiValue: String) {
    MOVIE("movie"),
    TV("tv");

    companion object {
        fun fromApi(value: String?): MediaType =
            entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) } ?: MOVIE
    }
}
