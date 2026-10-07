package com.meireles.todolist.data.datasource

import com.meireles.todolist.data.database.TaskEntity
import com.meireles.todolist.data.repositories.TaskCounts
import kotlinx.coroutines.flow.Flow

/**
 * Contrato da fonte de dados local responsável por gerenciar tarefas.
 *
 * Abstrai a implementação concreta (Room/SQLite) e facilita manutenção e
 * evolução — se a tecnologia de persistência mudar, apenas as implementações
 * desta interface precisam ser atualizadas.
 */
interface TaskLocalDatasource {
    /**
     * Cria uma nova tarefa no armazenamento local.
     *
     * @param taskEntity entidade a ser persistida; o `id` é ignorado.
     * @return o identificador gerado pela persistência.
     */
    suspend fun create(taskEntity: TaskEntity): Long

    /**
     * Retorna um fluxo reativo de tarefas, opcionalmente filtradas por status.
     *
     * O uso de [Flow] torna o acesso reativo: mudanças no banco são
     * automaticamente refletidas na coleta.
     *
     * @param isCompleted `true` para apenas concluídas, `false` para apenas
     *   em andamento, `null` para todas.
     */
    fun getAll(isCompleted: Boolean?): Flow<List<TaskEntity>>

    /**
     * Remove a tarefa identificada por [id] do armazenamento local.
     */
    suspend fun delete(id: Int)

    /**
     * Atualiza uma tarefa existente no armazenamento local.
     */
    suspend fun update(taskEntity: TaskEntity)

    /**
     * Retorna um fluxo reativo com a tarefa de [id], ou `null` se não existir.
     */
    fun getById(id: Int): Flow<TaskEntity?>

    /**
     * Alterna o estado de conclusão da tarefa identificada por [id].
     */
    suspend fun toggleStatus(id: Int)

    /**
     * Retorna um fluxo reativo com as contagens de tarefas por estado.
     */
    fun getCounts(): Flow<TaskCounts>
}
