package com.meireles.todolist_kotlin.data.repositories

import com.meireles.todolist_kotlin.data.datasource.TaskLocalDatasource
import com.meireles.todolist_kotlin.data.mappers.toDomain
import com.meireles.todolist_kotlin.data.mappers.toEntity
import com.meireles.todolist_kotlin.domain.model.Task
import com.meireles.todolist_kotlin.domain.repositories.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**Implementation of Repository.
 *
 * This repository acts as a bridge between the domain and the datasource,
 * in this case, local data source.
 *
 * It returns TaskEntity because it 'talks' to Room.
 *
 * Receives TaskLocalDatasource via dependency injection (Hilt), enabling separation of responsibilities.
 * */
class TaskRepositoryImpl @Inject constructor(
    private val local: TaskLocalDatasource
): TaskRepository {
    override suspend fun create(task: Task) : Long {
        return local.create(task.toEntity())
    }

    override fun getAll(isCompleted: Boolean?): Flow<List<Task>> {
        return local.getAll(isCompleted).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun delete(id: Int) {
        local.delete(id)
    }

    override suspend fun update(task: Task) {
        local.update(task.toEntity())
    }

    override fun getById(id: Int): Flow<Task?> {
        return local.getById(id).map { it?.toDomain() }
    }

    override suspend fun toggleStatus(id: Int) {
        local.toggleStatus(id)
    }

    override fun getNumber(): Flow<TaskCounts> {
        return local.getNumber()
    }
}