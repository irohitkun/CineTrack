package com.cinetrack.app.data.database

import androidx.room.TypeConverter
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.domain.model.WatchStatus

class Converters {
    @TypeConverter
    fun fromMediaType(value: MediaType): String = value.name

    @TypeConverter
    fun toMediaType(value: String): MediaType = MediaType.valueOf(value)

    @TypeConverter
    fun fromWatchStatus(value: WatchStatus): String = value.name

    @TypeConverter
    fun toWatchStatus(value: String): WatchStatus = WatchStatus.valueOf(value)
}
