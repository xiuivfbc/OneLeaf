package com.example.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class ToDoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    var title: String,
    var describe: String = "",
    var time: Long = 0, // 保留时间戳字段用于兼容
    var dateTime: java.time.LocalDateTime? = null, // 新增日期时间字段
    var enableAlarm: Boolean = false, // 是否启用闹钟
    var alarmTime: java.time.LocalDateTime? = null // 闹钟时间
)
