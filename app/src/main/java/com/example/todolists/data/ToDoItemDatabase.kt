package com.example.todolists.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ToDoItem::class], version = 1, exportSchema = false)
abstract class ToDoItemDatabase: RoomDatabase() {
    abstract fun todoItemDao(): ToDoItemDao
}