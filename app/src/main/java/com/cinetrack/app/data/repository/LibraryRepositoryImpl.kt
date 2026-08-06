package com.cinetrack.app.data.repository

import com.cinetrack.app.data.database.dao.UserMediaDao
import com.cinetrack.app.data.database.mapper.toDomain
import com.cinetrack.app.data.database.mapper.toEntity
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.domain.model.UserMedia
import com.cinetrack.app.domain.model.WatchStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val dao: UserMediaDao
) : LibraryRepository {

    override fun observeAll(): Flow<List<UserMedia>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByStatus(status: WatchStatus): Flow<List<UserMedia>> =
        dao.observeByStatus(status).map { list -> list.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<UserMedia>> =
        dao.observeFavorites().map { list -> list.map { it.toDomain() } }

    override fun observeItem(tmdbId: Int, mediaType: MediaType): Flow<UserMedia?> =
        dao.observeItem(tmdbId, mediaType).map { it?.toDomain() }

    override suspend fun getItem(tmdbId: Int, mediaType: MediaType): UserMedia? =
        dao.getItem(tmdbId, mediaType)?.toDomain()

    override suspend fun upsert(userMedia: UserMedia) {
        dao.upsert(userMedia.toEntity())
    }

    override suspend fun updateStatus(tmdbId: Int, mediaType: MediaType, status: WatchStatus) {
        val existing = dao.getItem(tmdbId, mediaType) ?: return
        val finishedAt = if (status == WatchStatus.COMPLETED) {
            existing.dateFinished ?: System.currentTimeMillis()
        } else {
            null
        }
        dao.update(
            existing.copy(
                status = status,
                dateFinished = finishedAt
            )
        )
    }

    override suspend fun setFavorite(tmdbId: Int, mediaType: MediaType, favorite: Boolean) {
        val existing = dao.getItem(tmdbId, mediaType) ?: return
        dao.update(existing.copy(isFavorite = favorite))
    }

    override suspend fun setPersonalRating(tmdbId: Int, mediaType: MediaType, rating: Float?) {
        val existing = dao.getItem(tmdbId, mediaType) ?: return
        dao.update(existing.copy(personalRating = rating))
    }

    override suspend fun setNotes(tmdbId: Int, mediaType: MediaType, notes: String) {
        val existing = dao.getItem(tmdbId, mediaType) ?: return
        dao.update(existing.copy(notes = notes))
    }

    override suspend fun setReview(tmdbId: Int, mediaType: MediaType, review: String) {
        val existing = dao.getItem(tmdbId, mediaType) ?: return
        dao.update(existing.copy(review = review))
    }

    override suspend fun updateTvProgress(
        tmdbId: Int,
        mediaType: MediaType,
        season: Int,
        episode: Int
    ) {
        val existing = dao.getItem(tmdbId, mediaType) ?: return
        dao.update(
            existing.copy(
                currentSeason = season.coerceAtLeast(1),
                currentEpisode = episode.coerceAtLeast(1)
            )
        )
    }

    override suspend fun markCompleted(tmdbId: Int, mediaType: MediaType) {
        updateStatus(tmdbId, mediaType, WatchStatus.COMPLETED)
    }

    override suspend fun remove(tmdbId: Int, mediaType: MediaType) {
        dao.delete(tmdbId, mediaType)
    }

    override suspend fun clearLibrary() {
        dao.clearAll()
    }

    override suspend fun countByStatus(status: WatchStatus): Int = dao.countByStatus(status)

    override suspend fun countByStatusAndType(status: WatchStatus, mediaType: MediaType): Int =
        dao.countByStatusAndType(status, mediaType)

    override suspend fun countFavorites(): Int = dao.countFavorites()

    override suspend fun averagePersonalRating(): Double? = dao.averagePersonalRating()

    override suspend fun getAll(): List<UserMedia> = dao.getAll().map { it.toDomain() }

    override suspend fun importAll(items: List<UserMedia>) {
        dao.upsertAll(items.map { it.toEntity() })
    }
}
