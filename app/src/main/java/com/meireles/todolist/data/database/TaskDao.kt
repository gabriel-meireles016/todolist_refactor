package com.meireles.todolist.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.meireles.todolist.data.repositories.TaskCounts
import kotlinx.coroutines.flow.Flow

/**
 * DAO do Room responsável pelo acesso à tabela `task`.
 *
 * As queries são reativas ([Flow]): alterações no banco são automaticamente
 * refletidas nos coletores.
 */
@Dao
interface TaskDao {
    /**
     * Insere uma nova tarefa e retorna o `rowId` gerado pelo SQLite.
     */
    @Insert
    suspend fun create(taskEntity: TaskEntity): Long

    /**
     * Retorna todas as tarefas, com filtro opcional por status de conclusão.
     *
     * @param isCompleted `true` para apenas concluídas, `false` para apenas
     *   em andamento, `null` para todas.
     */
    @Query(
        value =
            """
            SELECT * FROM ${TaskEntity.TABLE_NAME}
            WHERE (:isCompleted IS NULL OR ${TaskEntity.COLUMN_IS_COMPLETED} = :isCompleted)
            ORDER BY ${TaskEntity.COLUMN_ID}
            """,
    )
    fun getAll(isCompleted: Boolean?): Flow<List<TaskEntity>>

    /**
     * Remove a tarefa identificada por [id].
     */
    @Query("DELETE FROM ${TaskEntity.TABLE_NAME} WHERE ${TaskEntity.COLUMN_ID} = :id")
    suspend fun delete(id: Int)

    /**
     * Atualiza uma tarefa existente.
     */
    @Update
    suspend fun update(taskEntity: TaskEntity)

    /**
     * Retorna a tarefa identificada por [id], ou `null` se não existir.
     */
    @Query(
        value =
            """
            SELECT * FROM ${TaskEntity.TABLE_NAME}
            WHERE ${TaskEntity.COLUMN_ID} = :id
            LIMIT 1
            """,
    )
    fun getById(id: Int): Flow<TaskEntity?>

    /**
     * Alterna o estado de conclusão da tarefa identificada por [id].
     */
    @Query(
        value =
            """
            UPDATE ${TaskEntity.TABLE_NAME}
            SET ${TaskEntity.COLUMN_IS_COMPLETED} = NOT ${TaskEntity.COLUMN_IS_COMPLETED}
            WHERE ${TaskEntity.COLUMN_ID} = :id
            """,
    )
    suspend fun toggleStatus(id: Int)

    /**
     * Retorna as contagens de tarefas por estado (total, concluídas e em andamento).
     */
    @Query(
        value =
            """
            SELECT
                COUNT(*) AS total,
                COALESCE(SUM(CASE WHEN ${TaskEntity.COLUMN_IS_COMPLETED} = 1 THEN 1 ELSE 0 END),0)
                    AS completed,
                COALESCE(SUM(CASE WHEN ${TaskEntity.COLUMN_IS_COMPLETED} = 0 THEN 1 ELSE 0 END),0)
                    AS inProgress
            FROM ${TaskEntity.TABLE_NAME}
            """,
    )
    fun getCounts(): Flow<TaskCounts>
}
