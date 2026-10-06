package com.meireles.todolist_kotlin.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que representa uma tarefa persistida no banco SQLite.
 *
 * Esta classe pertence à camada de dados. Ela não deve vazar para o domínio:
 * use um mapper para convertê-la em [Task][com.meireles.todolist_kotlin.domain.model.Task].
 */
@Entity(tableName = TaskEntity.TABLE_NAME)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int = ID_NAO_PERSISTIDO,
    @ColumnInfo(name = COLUMN_TITLE)
    val title: String,
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    val description: String? = null,
    /** Instante de criação em milissegundos desde a epoch (Unix time). */
    @ColumnInfo(name = COLUMN_CREATED_AT)
    val createdAt: Long,
    @ColumnInfo(name = COLUMN_IS_COMPLETED)
    val isCompleted: Boolean = false,
) {
    companion object {
        const val TABLE_NAME = "task"

        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_IS_COMPLETED = "is_completed"

        /** Valor usado pelo Room para indicar que a entidade ainda não foi persistida. */
        const val ID_NAO_PERSISTIDO = 0
    }
}
