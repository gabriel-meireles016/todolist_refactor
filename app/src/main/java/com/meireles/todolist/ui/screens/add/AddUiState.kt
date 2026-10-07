package com.meireles.todolist.ui.screens.add

import com.meireles.todolist.domain.model.TaskId

/**
 * Estado imutável da tela de Adição/Edição de tarefa.
 *
 * @property isLoading indica operação em andamento.
 * @property isEditing `false` para criação de nova tarefa; `true` para edição
 *   de tarefa existente.
 * @property id identificador da tarefa em edição, ou `null` se for criação.
 * @property title campo editável de título no formulário.
 * @property description campo editável de descrição no formulário.
 * @property createdAt instante de criação em milissegundos (Unix time),
 *   fornecido pela camada de dados.
 * @property isCompleted estado de conclusão da tarefa.
 * @property isValid flag de validação do formulário (tipicamente: título obrigatório).
 * @property error mensagem de erro exibida na UI, ou `null` se não houver.
 * @property isSaved sinaliza que a tarefa foi salva com sucesso; a UI
 *   observa e navega de volta quando `true`.
 */
data class AddUiState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val id: TaskId? = null,
    val title: String = "",
    val description: String = "",
    val createdAt: Long? = null,
    val isCompleted: Boolean = false,
    val isValid: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
)
