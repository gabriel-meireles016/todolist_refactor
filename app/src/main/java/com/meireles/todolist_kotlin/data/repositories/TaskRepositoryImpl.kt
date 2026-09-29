package com.meireles.todolist_kotlin.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.meireles.todolist_kotlin.data.datasource.TaskLocalDatasource
import com.meireles.todolist_kotlin.data.mappers.toDomain
import com.meireles.todolist_kotlin.data.mappers.toEntity
import com.meireles.todolist_kotlin.domain.model.Task
import com.meireles.todolist_kotlin.domain.model.TaskId
import com.meireles.todolist_kotlin.domain.repositories.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementação de [TaskRepository] que atua como ponte entre o domínio
 * e a fonte de dados local.
 *
 * Responsabilidades desta classe:
 *  - Traduzir modelos de domínio ([Task]) para entidades de persistência
 *    ([com.meireles.todolist_kotlin.data.database.TaskEntity]) e vice-versa,
 *    usando os mappers [toEntity] e [toDomain].
 *  - Delegar as operações de persistência ao [TaskLocalDatasource],
 *    recebido via injeção de dependência (Hilt).
 *
 * A UI depende apenas da abstração [TaskRepository].
 * Isso mantém a camada de apresentação desacoplada dos detalhes de persistência.
 */
@RequiresApi(Build.VERSION_CODES.O)
class TaskRepositoryImpl @Inject constructor(
    private val local: TaskLocalDatasource
) : TaskRepository {

    override suspend fun create(task: Task) : TaskId {
        val generatedId = local.create(task.toEntity())
        return TaskId(generatedId)
    }

    override fun getAll(isCompleted: Boolean?): Flow<List<Task>> =
        local.getAll(isCompleted).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun delete(id: TaskId) {
        local.delete(id.value.toInt())
    }


    override suspend fun update(task: Task) {
        local.update(task.toEntity())
    }

    override fun getById(id: TaskId): Flow<Task?> =
        local.getById(id.value.toInt()).map { entity ->
            entity?.toDomain()
        }

    override suspend fun toggleStatus(id: TaskId) {
        local.toggleStatus(id.value.toInt())
    }

    override fun getCounts(): Flow<TaskCounts> =
        local.getCounts()

}
