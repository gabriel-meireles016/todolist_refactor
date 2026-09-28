package com.meireles.todolist_kotlin.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**Room Entity that represents a task.*/
@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val createdAt: Long,    // Long (milliseconds) converts to Date
    val isCompleted: Boolean = false
)