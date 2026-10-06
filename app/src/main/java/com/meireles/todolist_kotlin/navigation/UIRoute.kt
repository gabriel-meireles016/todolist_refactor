package com.meireles.todolist_kotlin.navigation

import kotlinx.serialization.Serializable

/**
 * Define as rotas de navegação do app com **type safety**.
 *
 * Cada destino é representado por um subtipo do sealed interface, o que
 * permite expressões `when` exaustivas e centraliza o gerenciamento de rotas.
 *
 * A anotação `@Serializable` permite a passagem segura de argumentos via JSON,
 * evitando concatenações frágeis de strings.
 */
@Serializable
sealed interface UIRoute {
    /** Tela inicial. */
    @Serializable
    data object Home : UIRoute

    /** Tela de adição de tarefa. */
    @Serializable
    data object Add : UIRoute

    /**
     * Tela de edição de tarefa.
     *
     * @property taskId identificador da tarefa a ser editada.
     */
    @Serializable
    data class Edit(
        val taskId: Int,
    ) : UIRoute
}
