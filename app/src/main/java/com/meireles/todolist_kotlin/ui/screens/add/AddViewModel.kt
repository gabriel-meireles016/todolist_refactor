package com.meireles.todolist_kotlin.ui.screens.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meireles.todolist_kotlin.data.database.TaskEntity
import com.meireles.todolist_kotlin.domain.model.Task
import com.meireles.todolist_kotlin.domain.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel that connects UI Add <-> Domain/Data.
 * */
@HiltViewModel
class AddViewModel @Inject constructor(
    /**Injected repository*/
    private val repo: TaskRepository,
    /**By this state, receives taskId via route args.*/
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    /**Task Identifier*/
    private val taskId: Int? = savedStateHandle["taskId"]

    /**Immutable screen state. Starts as 'loading' when a taskId exists (edit mode).*/
    private val _uiState =
        MutableStateFlow(AddUiState(isLoading = taskId != null, isEditing = taskId != null))

    /**Secure access of UI state.*/
    val uiState: StateFlow<AddUiState> = _uiState

    // Initializes AddViewModel. If it's an edit mode, load the task to fill in the fields.
    init {
        if (taskId != null) load(taskId)
    }

    /**Load task as Flow (reactive) and inject in state.*/
    private fun load(id: Int) {
        viewModelScope.launch {
            repo.getById(id)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Erro ao carregar tarefa."
                        )
                    }
                }
                .collectLatest { task ->
                    if (task == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Tarefa não encontrada."
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                id = task.id,
                                title = task.title,
                                description = task.description.orEmpty(),
                                isCompleted = task.isCompleted,
                                isValid = task.title.isNotBlank(),
                                error = null
                            )
                        }
                    }
                }
        }
    }

    /**Update title on the edit mode.*/
    fun onTitleChange(newTitle: String) {
        _uiState.update {
            it.copy(
                title = newTitle,
                isValid = newTitle.isNotBlank(),
                error = null
            )
        }
    }

    /**Update description on the edit mode.*/
    fun onDescriptionChange(newDesc: String) {
        _uiState.update {
            it.copy(
                description = newDesc,
                error = null
            )
        }
    }

    /**Save fields and create task.*/
    fun save() {
        val state = _uiState.value
        if (!state.isValid) {
            _uiState.update {
                it.copy(
                    error = "O título não pode estar vazio."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            runCatching {
                if (state.isEditing) {
                    val task = Task(
                        id = state.id ?: error("ID nulo em modo edição."),
                        title = state.title.trim(),
                        description = state.description.ifBlank { null },
                        createdAt = state.createdAt ?: System.currentTimeMillis(),
                        isCompleted = state.isCompleted,
                    )
                    repo.update(task)
                } else {
                    val task = Task(
                        id = 0,
                        title = state.title.trim(),
                        description = state.description.ifBlank { null },
                        createdAt = System.currentTimeMillis(),
                        isCompleted = false
                    )
                    repo.create(task)
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Falha ao salvar."
                    )
                }
            }
        }
    }

}