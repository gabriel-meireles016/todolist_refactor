package com.meireles.todolist_kotlin.domain.repositories

import com.meireles.todolist_kotlin.data.repositories.TaskCounts
import com.meireles.todolist_kotlin.domain.model.Task
import com.meireles.todolist_kotlin.domain.model.TaskId
import kotlinx.coroutines.flow.Flow

// Ui should depend on this interface and not directly on DAOs or datasources

/**
 * Contrato da camada de domínio para operações sobre [Task].
 *
 * A UI deve depender **apenas** desta interface — nunca diretamente de DAOs
 * ou fontes de dados. Isso mantém a camada de apresentação independente de
 * detalhes de persistência (banco, cache, rede).
 */
interface TaskRepository {

    /**
     * Cria uma nova [Task] e retorna o identificador gerado.
     *
     * @param task tarefa a ser persistida; o `id` é ignorado e substituído
     *   pelo valor gerado pela camada de persistência.
     * @return o [TaskId] atribuído à tarefa criada.
     */
    suspend fun create(task: Task) : TaskId

    /**
     * Retorna um fluxo reativo de tarefas, opcionalmente filtradas por status.
     *
     * @param isCompleted `true` para apenas concluídas, `false` para apenas
     *   em andamento, `null` para todas.
     */
    fun getAll(isCompleted: Boolean?): Flow<List<Task>>

    /**
     * Remove a tarefa identificada por [id].
     */
    suspend fun delete(id: TaskId)

    /**
     * Atualiza os dados de uma [Task] já existente.
     */
    suspend fun update(task: Task)

    /**
     * Retorna um fluxo reativo com a tarefa de [id], ou `null` se não existir.
     */
    fun getById(id: TaskId): Flow<Task?>

    /**
     * Alterna o estado de conclusão da tarefa identificada por [id].
     */
    suspend fun toggleStatus(id: TaskId)

    /**
     * Retorna um fluxo reativo com as contagens de tarefas por estado.
     */
    fun getCounts(): Flow<TaskCounts>

}
