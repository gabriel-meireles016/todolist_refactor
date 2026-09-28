package com.meireles.todolist_kotlin.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meireles.todolist_kotlin.domain.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel that connects UI Home <-> Domain/Data.
 * */
@HiltViewModel
class HomeViewModel @Inject constructor(
    /**Injected repository*/private val repo: TaskRepository
) : ViewModel() {
    /**Immutable screen state. Starts as 'loading' when a taskId exists (edit mode).*/
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    // Coroutines states. Jobs to control collections.
    // When changing filters or restarting observation, we cancel the previous one to avoid
    // duplicate collections and leaks.
    /**
     * Job responsible for observing the task list.
     *
     * It is canceled and recreated whenever the filter changes to avoid multiple simultaneous
     * collections.
     * */
    private var tasksJob: Job? = null
    /**
     * Job tha observes the aggregated counts (total, completed, active).
     *
     * Kept separate so that the counts update independently of the list.
     * */
    private var countsJob: Job? = null
    /**
     * Job used to observe/load a specific task by ID.
     *
     * It's canceled before starting a new search, avoiding competing results.
     * */
    private var taskByIdJob: Job? = null

    // Each observer cancel previous job before starts another one.
    // Avoid duplicated collects or leaks when filter changes
    init {
        observeTasks()
        observeCounts()
    }

    /** Define task filter (all/active/completed)*/
    fun setFilter(isCompleted: Boolean?) {
        _uiState.update {
            it.copy(
                filterCompleted = isCompleted,
                isLoading = true
            )
        }
        observeTasks()
    }

    /**Observe tasks according to the current filter.*/
    private fun observeTasks() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            repo.getAll(_uiState.value.filterCompleted)
                // Use catch to handle errors in the flow.
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Erro ao carregar tarefas"
                        )
                    }
                    // If flow is re-emitted, it cancels the current collection and processes only
                    // the most recent one
                }.collectLatest { list ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tasks = list,
                            error = null
                        )
                    }
                }
        }
    }

    /**Observe aggregated counts*/
    private fun observeCounts() {
        countsJob?.cancel()
        countsJob = viewModelScope.launch {
            repo.getNumber()
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message)
                    }
                }
                .collectLatest { counts ->
                    _uiState.update {
                        it.copy(
                            counts = counts,
                            error = null
                        )
                    }
                }
        }
    }

    /**Delete action that reports failures in 'error'.*/
    fun delete(id: Int) = viewModelScope.launch {
        runCatching { repo.delete(id) }
            .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
    }

    /**Toggle status action that reports failures in 'error'.*/
    fun toggleStatus(id: Int) = viewModelScope.launch {
        runCatching { repo.toggleStatus(id) }
            .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
    }

}