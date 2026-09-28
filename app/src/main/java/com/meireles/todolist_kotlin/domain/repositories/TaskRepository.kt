package com.meireles.todolist_kotlin.domain.repositories

import com.meireles.todolist_kotlin.data.repositories.TaskCounts
import com.meireles.todolist_kotlin.domain.model.Task
import kotlinx.coroutines.flow.Flow

// Ui should depend on this interface and not directly on DAOs or datasources

/**
 * Domain layer contract. The UI depend ONLY on this contract; that is, no class in this layer
 * should know details about database.
 * */
interface TaskRepository {

    /**Creates a new task and returns the generated ID.*/
    suspend fun create(task: Task) : Long

    /**Get tasks, optionally filtered by completion status.*/
    fun getAll(isCompleted: Boolean?): Flow<List<Task>>

    /**Deletes a task by its ID.*/
    suspend fun delete(id: Int)

    /**Updates a task.*/
    suspend fun update(task: Task)

    /**Gets a task by ID.*/
    fun getById(id: Int): Flow<Task?>

    /**Toggles the completion status of a task by ID.*/
    suspend fun toggleStatus(id: Int)

    /**Returns reactive counters about task completion status.*/
    fun getNumber(): Flow<TaskCounts>

}