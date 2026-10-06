package com.meireles.todolist_kotlin.ui.components

/**
 * Filtros disponíveis para exibição de tarefas na UI.
 */
enum class TaskFilter {
    /** Exibe todas as tarefas. */
    ALL,

    /** Exibe apenas tarefas em andamento. */
    ACTIVE,

    /** Exibe apenas tarefas concluídas. */
    COMPLETED,
}

/**
 * Cicla entre os três filtros na ordem ALL → ACTIVE → COMPLETED → ALL.
 *
 * Usado quando o usuário toca no chip de filtro na barra superior.
 */
fun TaskFilter.next(): TaskFilter =
    when (this) {
        TaskFilter.ALL -> TaskFilter.ACTIVE
        TaskFilter.ACTIVE -> TaskFilter.COMPLETED
        TaskFilter.COMPLETED -> TaskFilter.ALL
    }

/**
 * Converte o filtro para o valor booleano esperado pelo repositório/DAO.
 *
 * O `null` representa "sem filtro" (todas as tarefas), enquanto `false`
 * e `true` filtram por tarefas em andamento e concluídas, respectivamente.
 *
 * @return `null` para [TaskFilter.ALL], `false` para [TaskFilter.ACTIVE],
 *   `true` para [TaskFilter.COMPLETED].
 */
fun TaskFilter.toBooleanOrNull(): Boolean? =
    when (this) {
        TaskFilter.ALL -> null
        TaskFilter.ACTIVE -> false
        TaskFilter.COMPLETED -> true
    }
