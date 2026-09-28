package com.meireles.todolist_kotlin.domain.model

/**
 * Domain Model that represents a task. Independent of the database.
 */
data class Task(
    val id: Int,
    val title: String,
    val description: String?,
    val createdAt: Long,
    val isCompleted: Boolean
)
