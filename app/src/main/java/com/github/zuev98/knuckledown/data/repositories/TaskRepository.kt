package com.github.zuev98.knuckledown.data.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.github.zuev98.knuckledown.data.db.TaskDatabase
import com.github.zuev98.knuckledown.data.entities.Task
import com.github.zuev98.knuckledown.util.Constants.DATABASE_NAME
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TaskRepository(context: Context) {
    private val database: TaskDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            TaskDatabase::class.java,
            DATABASE_NAME
        ).build()

    suspend fun getTask(taskId: Int): Task = database.taskDao().getTask(taskId)

    fun getInboxTasks(): Flow<List<Task>> = database.taskDao().getInboxTasks()

    fun getActionTasks(): Flow<List<Task>> = database.taskDao().getActionTasks()

    suspend fun getLastId(): Int = database.taskDao().getLastId()

    fun getTasksByDate(date: Date, dateEnd: Date): LiveData<List<Task>?> =
        database.taskDao().getTasksByDate(date, dateEnd)

    suspend fun updateTask(task: Task) {
        database.taskDao().updateTask(task)
    }

    suspend fun addTask(task: Task) {
        database.taskDao().addTask(task)
    }

    suspend fun deleteTask(task: Task) {
        database.taskDao().deleteTask(task)
    }

    companion object {
        private var INSTANCE: TaskRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TaskRepository(context)
            }
        }

        fun get(): TaskRepository {
            return INSTANCE
                ?: throw IllegalStateException("TaskRepository must be initialized")
        }
    }
}