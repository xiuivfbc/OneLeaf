package com.example.todolist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface ToDoItemDao {
    @Insert
    suspend fun insert(item: ToDoItem): Long

    @Update
    suspend fun update(item: ToDoItem)

    @Delete
    suspend fun delete(item: ToDoItem)

    @Query("SELECT * FROM todo_items WHERE id = :id")
    fun getItem(id: Long): Flow<ToDoItem>

    @Query("SELECT * FROM todo_items ORDER BY time DESC")
    fun getAllItems(): Flow<List<ToDoItem>>

    @Query("SELECT * FROM todo_items WHERE enableAlarm = 1 AND alarmTime <= :endTime ORDER BY alarmTime ASC")
    fun getUpcomingAlarms(endTime: Long?): Flow<List<ToDoItem>>
}
