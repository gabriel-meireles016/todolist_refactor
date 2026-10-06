package com.meireles.todolist_kotlin.data.datasource

import com.meireles.todolist_kotlin.data.database.TaskDao
import com.meireles.todolist_kotlin.data.database.TaskEntity
import com.meireles.todolist_kotlin.data.repositories.TaskCounts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementação de [TaskLocalDatasource].
 *
 * Atua como ponte entre a camada de dados e o Room: delega as operações
 * ao [TaskDao], mas expõe apenas o contrato [TaskLocalDatasource] para o
 * restante do sistema. O [TaskDao] é injetado via Hilt.
 */
class TaskLocalDatasourceImpl
    @Inject
    constructor(
        private val taskDao: TaskDao,
    ) : TaskLocalDatasource {
        override suspend fun create(taskEntity: TaskEntity): Long = taskDao.create(taskEntity)

        override fun getAll(isCompleted: Boolean?): Flow<List<TaskEntity>> = taskDao.getAll(isCompleted)

        override suspend fun delete(id: Int) {
            taskDao.delete(id)
        }

        override suspend fun update(taskEntity: TaskEntity) {
            taskDao.update(taskEntity)
        }

        override fun getById(id: Int): Flow<TaskEntity?> = taskDao.getById(id)

        override suspend fun toggleStatus(id: Int) {
            taskDao.toggleStatus(id)
        }

        override fun getCounts(): Flow<TaskCounts> = taskDao.getCounts()
    }
