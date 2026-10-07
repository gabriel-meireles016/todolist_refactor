package com.meireles.todolist.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meireles.todolist.domain.model.TaskId
import com.meireles.todolist.domain.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Mensagem de erro padrão ao carregar tarefas. */
private const val ERRO_AO_CARREGAR_TAREFAS = "Erro ao carregar tarefas"

/**
 * ViewModel da tela Home.
 *
 * Conecta a UI ao domínio/dados: observa a lista de tarefas e as contagens
 * de forma reativa, e expõe ações (filtrar, deletar, alternar status) que
 * atualizam o estado da tela.
 */
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val repository: TaskRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
        val uiState: StateFlow<HomeUiState> = _uiState

        /**
         * Job que observa a lista de tarefas.
         * Cancelado e recriado sempre que o filtro muda.
         */
        private var tasksJob: Job? = null

        /**
         * Job que observa as contagens agregadas (total, concluídas, ativas).
         * Mantido separado para que as contagens atualizem independentemente da lista.
         */
        private var countsJob: Job? = null

        init {
            observeTasks()
            observeCounts()
        }

        /**
         * Define o filtro de exibição de tarefas.
         *
         * @param isCompleted `true` para concluídas, `false` para em andamento,
         *   `null` para todas.
         */
        fun setFilter(isCompleted: Boolean?) {
            _uiState.update {
                it.copy(
                    filterCompleted = isCompleted,
                    isLoading = true,
                )
            }
            observeTasks()
        }

        /**
         * Observa a lista de tarefas de acordo com o filtro atual.
         *
         * Cancela a observação anterior antes de iniciar uma nova, evitando
         * coletas duplicadas.
         */
        private fun observeTasks() {
            tasksJob?.cancel()
            tasksJob =
                viewModelScope.launch {
                    repository
                        .getAll(_uiState.value.filterCompleted)
                        .catch { e ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = e.message ?: ERRO_AO_CARREGAR_TAREFAS,
                                )
                            }
                        }.collectLatest { tasks ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    tasks = tasks,
                                    error = null,
                                )
                            }
                        }
                }
        }

        /**
         * Observa as contagens agregadas de tarefas.
         */
        private fun observeCounts() {
            countsJob?.cancel()
            countsJob =
                viewModelScope.launch {
                    repository
                        .getCounts()
                        .catch { e ->
                            _uiState.update {
                                it.copy(error = e.message ?: ERRO_AO_CARREGAR_TAREFAS)
                            }
                        }.collectLatest { counts ->
                            _uiState.update {
                                it.copy(
                                    counts = counts,
                                    error = null,
                                )
                            }
                        }
                }
        }

        /**
         * Remove a tarefa de [id].
         * Falhas são registradas em [HomeUiState.error].
         */
        fun delete(id: TaskId) =
            viewModelScope.launch {
                runCatching { repository.delete(id) }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                error = e.message ?: ERRO_AO_CARREGAR_TAREFAS,
                            )
                        }
                    }
            }

        /**
         * Alterna o status de conclusão da tarefa de [id].
         * Falhas são registradas em [HomeUiState.error].
         */
        fun toggleStatus(id: TaskId) =
            viewModelScope.launch {
                runCatching { repository.toggleStatus(id) }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                error = e.message ?: ERRO_AO_CARREGAR_TAREFAS,
                            )
                        }
                    }
            }
    }
