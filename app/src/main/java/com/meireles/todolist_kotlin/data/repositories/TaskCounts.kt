package com.meireles.todolist_kotlin.data.repositories

/**Helper class that stores task counts with their filters*/
data class TaskCounts(
    val total: Int,
    val completed: Int,
    val inProgress: Int
)
