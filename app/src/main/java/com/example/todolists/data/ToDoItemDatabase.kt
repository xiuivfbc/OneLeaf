package com.example.todolists.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ToDoItem::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ToDoItemDatabase : RoomDatabase() {
    abstract fun todoItemDao(): ToDoItemDao

    companion object {
        fun getDatabase(context: Context): ToDoItemDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ToDoItemDatabase::class.java,
                "todo_item_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
