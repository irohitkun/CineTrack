package com.cinetrack.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cinetrack.app.data.database.dao.UserMediaDao
import com.cinetrack.app.data.database.entity.UserMediaEntity

@Database(
    entities = [UserMediaEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userMediaDao(): UserMediaDao
}
