package com.meireles.todolist.domain.model

import java.time.Instant

/**
 * Representa uma tarefa no domínio da aplicação.
 * Independente de banco de dados e de frameworks.
 *
 * @property id identificador único da tarefa.
 * @property title título curto e obrigatório.
 * @property description descrição opcional; `null` quando o usuário não informou.
 * @property createdAt instante em que a tarefa foi criada.
 * @property isCompleted indica se a tarefa foi concluída.
 */
data class Task(
    val id: TaskId,
    val title: Title,
    val description: Description?,
    val createdAt: Instant,
    val isCompleted: Boolean,
)

/** Identificador único de uma [Task]. */
@JvmInline
value class TaskId(
    val value: Long,
)

/** Título de uma [Task]. */
@JvmInline
value class Title(
    val value: String,
)

/** Descrição opcional de uma [Task]. */
@JvmInline
value class Description(
    val value: String,
)
