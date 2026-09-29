package com.meireles.todolist_kotlin.data.repositories

/**
 * Agrega a contagem de tarefas por estado, usada para exibir
 * resumos e aplicar filtros na interface.
 *
 * @property total número total de tarefas (soma de concluídas + em andamento).
 * @property completed número de tarefas concluídas.
 * @property inProgress número de tarefas em andamento.
 */
data class TaskCounts(
    val total: Int,
    val completed: Int,
    val inProgress: Int
)
