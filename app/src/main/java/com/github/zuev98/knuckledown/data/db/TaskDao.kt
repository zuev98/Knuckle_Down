package com.github.zuev98.knuckledown.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.zuev98.knuckledown.data.entities.Task
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TaskDao {
    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTask(id: Int): Task

    @Query("SELECT * FROM task WHERE isInbox = 1")
    fun getInboxTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date IS NULL AND isInbox = 0")
    fun getActionTasks(): Flow<List<Task>>

    @Query("SELECT id FROM task ORDER BY id DESC LIMIT 1")
    suspend fun getLastId(): Int

    @Query("SELECT * FROM task WHERE date BETWEEN :date AND :dateEnd ORDER BY date ASC")
    fun getTasksByDate(date: Date, dateEnd: Date): LiveData<List<Task>?>

    @Update
    suspend fun updateTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}