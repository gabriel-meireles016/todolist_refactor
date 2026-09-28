package com.meireles.todolist_kotlin.data.datasource

import com.meireles.todolist_kotlin.data.database.TaskDao
import com.meireles.todolist_kotlin.data.database.TaskEntity
import com.meireles.todolist_kotlin.data.repositories.TaskCounts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of the local data source.
 *
 * This class acts as a bridge between the domain and the database (Room),
 * delegating operations to the DAO, but exposing only the TaskLocalDatasource interface
 *
 * Receives TaskDao via dependency injection (Hilt), enabling separation of responsibilities.
 * */
class TaskLocalDatasourceImpl @Inject constructor(
    private val taskDao: TaskDao
): TaskLocalDatasource{
    override suspend fun create(taskEntity: TaskEntity) : Long {
        return taskDao.create(taskEntity)
    }

    override fun getAll(isCompleted: Boolean?): Flow<List<TaskEntity>> {
        return taskDao.getAll(isCompleted)
    }

    override suspend fun delete(id: Int) {
        taskDao.delete(id)
    }

    override suspend fun update(taskEntity: TaskEntity) {
        taskDao.update(taskEntity)
    }

    override fun getById(id: Int): Flow<TaskEntity?> {
        return taskDao.getById(id)
    }

    override suspend fun toggleStatus(id: Int) {
        taskDao.toggleStatus(id)
    }

    override fun getNumber(): Flow<TaskCounts> {
        return taskDao.getNumber()
    }
}