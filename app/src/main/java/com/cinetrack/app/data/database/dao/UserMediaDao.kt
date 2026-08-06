package com.cinetrack.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cinetrack.app.data.database.entity.UserMediaEntity
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.domain.model.WatchStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserMediaDao {

    @Query("SELECT * FROM user_media ORDER BY dateAdded DESC")
    fun observeAll(): Flow<List<UserMediaEntity>>

    @Query("SELECT * FROM user_media WHERE status = :status ORDER BY dateAdded DESC")
    fun observeByStatus(status: WatchStatus): Flow<List<UserMediaEntity>>

    @Query("SELECT * FROM user_media WHERE isFavorite = 1 ORDER BY dateAdded DESC")
    fun observeFavorites(): Flow<List<UserMediaEntity>>

    @Query("SELECT * FROM user_media WHERE tmdbId = :tmdbId AND mediaType = :mediaType LIMIT 1")
    fun observeItem(tmdbId: Int, mediaType: MediaType): Flow<UserMediaEntity?>

    @Query("SELECT * FROM user_media WHERE tmdbId = :tmdbId AND mediaType = :mediaType LIMIT 1")
    suspend fun getItem(tmdbId: Int, mediaType: MediaType): UserMediaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: UserMediaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<UserMediaEntity>)

    @Update
    suspend fun update(entity: UserMediaEntity)

    @Query("DELETE FROM user_media WHERE tmdbId = :tmdbId AND mediaType = :mediaType")
    suspend fun delete(tmdbId: Int, mediaType: MediaType)

    @Query("DELETE FROM user_media")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM user_media WHERE status = :status AND mediaType = :mediaType")
    suspend fun countByStatusAndType(status: WatchStatus, mediaType: MediaType): Int

    @Query("SELECT COUNT(*) FROM user_media WHERE status = :status")
    suspend fun countByStatus(status: WatchStatus): Int

    @Query("SELECT COUNT(*) FROM user_media WHERE isFavorite = 1")
    suspend fun countFavorites(): Int

    @Query("SELECT AVG(personalRating) FROM user_media WHERE personalRating IS NOT NULL")
    suspend fun averagePersonalRating(): Double?

    @Query("SELECT * FROM user_media")
    suspend fun getAll(): List<UserMediaEntity>
}
