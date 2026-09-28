package com.meireles.todolist_kotlin.data.mappers

import com.meireles.todolist_kotlin.data.database.TaskEntity
import com.meireles.todolist_kotlin.domain.model.Task

/**
 * Converts the database entity (Room) to the domain model.
 * */
fun TaskEntity.toDomain(): Task =
    Task(
        id = id,
        title = title,
        description = description,
        createdAt = createdAt,
        isCompleted = isCompleted
    )

/**
 * Converts the the domain model to database entity (Room).
 * */
fun Task.toEntity(): TaskEntity =
    TaskEntity(
        id = id,
        title = title,
        description = description,
        createdAt = createdAt,
        isCompleted = isCompleted
    )