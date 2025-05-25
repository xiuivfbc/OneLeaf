package com.example.todolists.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class ToDoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var title: String,
    var describe: String, // 建议字段名为 describe
    var time: Long // 以时间戳存储
)
