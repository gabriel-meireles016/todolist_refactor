package com.meireles.todolist_kotlin.ui.screens.add

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meireles.todolist_kotlin.domain.model.Description
import com.meireles.todolist_kotlin.domain.model.Task
import com.meireles.todolist_kotlin.domain.model.TaskId
import com.meireles.todolist_kotlin.domain.model.Title
import com.meireles.todolist_kotlin.domain.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/** Chave do argumento de navegação que identifica a tarefa em edição. */
private const val ARG_TASK_ID = "taskId"

/** Mensagens de erro padrão da tela. */
private const val ERRO_CARREGAR_TAREFA = "Erro ao carregar tarefa."
private const val ERRO_TAREFA_NAO_ENCONTRADA = "Tarefa não encontrada."
private const val ERRO_TITULO_VAZIO = "O título não pode estar vazio."
private const val ERRO_SALVAR_TAREFA = "Falha ao salvar."

/**
 * ViewModel da tela de Adição/Edição de tarefa.
 *
 * Conecta a UI ao domínio/dados: em modo edição, carrega a tarefa pelo
 * [SavedStateHandle]; expõe ações de formulário (título, descrição) e
 * salvamento (criação ou atualização).
 */
@HiltViewModel
class AddViewModel
    @Inject
    constructor(
        private val repository: TaskRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val taskId: TaskId? = savedStateHandle.get<Int>(ARG_TASK_ID)?.let { TaskId(it.toLong()) }

        private val _uiState =
            MutableStateFlow(
                AddUiState(
                    isLoading = taskId != null,
                    isEditing = taskId != null,
                ),
            )
        val uiState: StateFlow<AddUiState> = _uiState

        /** Job que observa a tarefa em edição. Cancelado antes de nova observação. */
        private var loadJob: Job? = null

        init {
            taskId?.let(::load)
        }

        /**
         * Carrega a tarefa de [id] de forma reativa e atualiza o estado do formulário.
         */
        private fun load(id: TaskId) {
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    repository
                        .getById(id)
                        .catch { e ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = e.message ?: ERRO_CARREGAR_TAREFA,
                                )
                            }
                        }.collectLatest { task ->
                            if (task == null) {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        error = ERRO_TAREFA_NAO_ENCONTRADA,
                                    )
                                }
                            } else {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        id = task.id,
                                        title = task.title.value,
                                        description = task.description?.value.orEmpty(),
                                        isCompleted = task.isCompleted,
                                        isValid = task.title.value.isNotBlank(),
                                        error = null,
                                    )
                                }
                            }
                        }
                }
        }

        /**
         * Atualiza o título no formulário.
         */
        fun onTitleChange(newTitle: String) {
            _uiState.update {
                it.copy(
                    title = newTitle,
                    isValid = newTitle.isNotBlank(),
                    error = null,
                )
            }
        }

        /**
         * Atualiza a descrição no formulário.
         */
        fun onDescriptionChange(newDescription: String) {
            _uiState.update {
                it.copy(
                    description = newDescription,
                    error = null,
                )
            }
        }

        /**
         * Salva a tarefa: cria uma nova ou atualiza a existente.
         *
         * Falhas são registradas em [AddUiState.error]; sucesso marca
         * [AddUiState.isSaved] como `true`.
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun save() {
            val state = _uiState.value
            if (!state.isValid) {
                _uiState.update { it.copy(error = ERRO_TITULO_VAZIO) }
                return
            }

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }

                runCatching {
                    val task = state.toDomainTask()
                    if (state.isEditing) {
                        repository.update(task)
                    } else {
                        repository.create(task)
                    }
                }.onSuccess {
                    _uiState.update {
                        it.copy(isLoading = false, isSaved = true)
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: ERRO_SALVAR_TAREFA,
                        )
                    }
                }
            }
        }
    }

/**
 * Converte o estado do formulário em uma [Task] do domínio.
 *
 * Em modo edição, reaproveita o `id` e o `createdAt` originais; em modo
 * criação, gera um novo `createdAt` e marca a tarefa como não concluída.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun AddUiState.toDomainTask(): Task {
    val id = this.id ?: TaskId(0L)
    val createdAt = this.createdAt ?: Instant.now().toEpochMilli()

    return Task(
        id = id,
        title = Title(title.trim()),
        description = description.ifBlank { null }?.let(::Description),
        createdAt = Instant.ofEpochMilli(createdAt),
        isCompleted = if (isEditing) isCompleted else false,
    )
}
