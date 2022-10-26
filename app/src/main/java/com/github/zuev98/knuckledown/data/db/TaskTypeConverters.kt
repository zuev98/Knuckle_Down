package com.github.zuev98.knuckledown.data.db

import androidx.room.TypeConverter
import java.util.*

class TaskTypeConverters {
    @TypeConverter
    fun fromDate(date: Date?) = date?.time

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?) = millisSinceEpoch?.let { Date(it) }
}