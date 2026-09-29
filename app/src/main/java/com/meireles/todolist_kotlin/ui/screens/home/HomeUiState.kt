package com.meireles.todolist_kotlin.ui.screens.home

import com.meireles.todolist_kotlin.data.repositories.TaskCounts
import com.meireles.todolist_kotlin.domain.model.Task

/**
 * Estado imutável da tela Home.
 *
 * @property isLoading indica operação em andamento.
 * @property tasks lista de tarefas exibidas na UI.
 * @property selectedTask tarefa selecionada, usada para destacar o item.
 * @property counts agregado com contagens (total, concluídas, ativas).
 * @property filterCompleted filtro atual por status de conclusão
 *   (`true` = concluídas, `false` = em andamento, `null` = todas).
 * @property error mensagem de erro exibida na UI, ou `null` se não houver.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val selectedTask: Task? = null,
    val counts: TaskCounts? = null,
    val filterCompleted: Boolean? = null,
    val error: String? = null,
)
