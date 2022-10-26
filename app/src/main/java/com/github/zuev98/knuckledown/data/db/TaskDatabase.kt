package com.github.zuev98.knuckledown.data.db

import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.TypeConverters
import com.github.zuev98.knuckledown.data.entities.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(TaskTypeConverters::class)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
}