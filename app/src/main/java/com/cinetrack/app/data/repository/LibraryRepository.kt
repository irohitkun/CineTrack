package com.cinetrack.app.data.repository

import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.domain.model.UserMedia
import com.cinetrack.app.domain.model.WatchStatus
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun observeAll(): Flow<List<UserMedia>>
    fun observeByStatus(status: WatchStatus): Flow<List<UserMedia>>
    fun observeFavorites(): Flow<List<UserMedia>>
    fun observeItem(tmdbId: Int, mediaType: MediaType): Flow<UserMedia?>

    suspend fun getItem(tmdbId: Int, mediaType: MediaType): UserMedia?
    suspend fun upsert(userMedia: UserMedia)
    suspend fun updateStatus(tmdbId: Int, mediaType: MediaType, status: WatchStatus)
    suspend fun setFavorite(tmdbId: Int, mediaType: MediaType, favorite: Boolean)
    suspend fun setPersonalRating(tmdbId: Int, mediaType: MediaType, rating: Float?)
    suspend fun setNotes(tmdbId: Int, mediaType: MediaType, notes: String)
    suspend fun setReview(tmdbId: Int, mediaType: MediaType, review: String)
    suspend fun updateTvProgress(
        tmdbId: Int,
        mediaType: MediaType,
        season: Int,
        episode: Int
    )
    suspend fun markCompleted(tmdbId: Int, mediaType: MediaType)
    suspend fun remove(tmdbId: Int, mediaType: MediaType)
    suspend fun clearLibrary()

    suspend fun countByStatus(status: WatchStatus): Int
    suspend fun countByStatusAndType(status: WatchStatus, mediaType: MediaType): Int
    suspend fun countFavorites(): Int
    suspend fun averagePersonalRating(): Double?
    suspend fun getAll(): List<UserMedia>
    suspend fun importAll(items: List<UserMedia>)
}
