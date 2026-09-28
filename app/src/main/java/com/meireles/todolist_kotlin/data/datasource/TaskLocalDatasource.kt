package com.meireles.todolist_kotlin.data.datasource

import com.meireles.todolist_kotlin.data.database.TaskEntity
import com.meireles.todolist_kotlin.data.repositories.TaskCounts
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction of the local data source responsible for managing tasks.
 * Hides the Room Implementation and facilitates maintenance and evolution.
 * */
interface TaskLocalDatasource {

    /**Creates a task using local storage.*/
    suspend fun create(taskEntity: TaskEntity) : Long

    /**Gets all tasks using local storage.*/
    fun getAll(isCompleted: Boolean?): Flow<List<TaskEntity>>
    // Flow makes access more reactive by implementing changes automatically.

    /**Deletes a task by ID using local storage.*/
    suspend fun delete(id: Int)

    /**Updates a task using local storage.*/
    suspend fun update(taskEntity: TaskEntity)

    /**Gets a task by ID using local storage.*/
    fun getById(id: Int): Flow<TaskEntity?>

    /**Toggles task status using local storage.*/
    suspend fun toggleStatus(id: Int)

    /**Gets number of all, completed and in progress tasks using local storage.*/
    fun getNumber(): Flow<TaskCounts>

}