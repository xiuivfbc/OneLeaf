package com.example.todolist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ToDoItem::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ToDoItemDatabase : RoomDatabase() {
    abstract fun todoItemDao(): ToDoItemDao
}
