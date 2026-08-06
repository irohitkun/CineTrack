package com.cinetrack.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.domain.model.WatchStatus

/**
 * User-tracking row only. TMDB catalog is never mirrored here.
 * [title]/[posterPath] (and optional display fields) exist solely for offline Library UI.
 */
@Entity(
    tableName = "user_media",
    primaryKeys = ["tmdbId", "mediaType"],
    indices = [
        Index(value = ["status"]),
        Index(value = ["isFavorite"])
    ]
)
data class UserMediaEntity(
    val tmdbId: Int,
    val mediaType: MediaType,
    val status: WatchStatus,
    val title: String,
    val posterPath: String?,
    val backdropPath: String? = null,
    val runtimeMinutes: Int? = null,
    /** Comma-separated genre IDs for lightweight stats (most-watched genre). */
    val genreIds: String = "",
    val personalRating: Float? = null,
    val isFavorite: Boolean = false,
    val notes: String = "",
    val review: String = "",
    val currentSeason: Int = 1,
    val currentEpisode: Int = 1,
    val totalSeasons: Int? = null,
    val totalEpisodes: Int? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val dateFinished: Long? = null
)
