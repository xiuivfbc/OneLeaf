package com.example.todolist.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toInstant(value: Instant?): Long? {
        return value?.toEpochMilli()
    }
}
