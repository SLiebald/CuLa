package com.sliebald.cula.data.database

import androidx.room.TypeConverter
import java.util.*

/**
 * Custom converter for date/time.
 */
class DateConverter {

    @TypeConverter
    fun timestampToDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}