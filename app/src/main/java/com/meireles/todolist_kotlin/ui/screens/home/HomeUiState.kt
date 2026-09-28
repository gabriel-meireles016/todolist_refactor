package com.meireles.todolist_kotlin.ui.screens.home

import com.meireles.todolist_kotlin.data.repositories.TaskCounts
import com.meireles.todolist_kotlin.domain.model.Task

/**Immutable state of the Home screen.*/
data class HomeUiState(
    /**Indicates an ongoing operation.*/
    val isLoading: Boolean = false,
    /**List of tasks displayed in the UI.*/
    val tasks: List<Task> = emptyList(),
    /**Selected task, makes the item stand out.*/
    val selectedTask: Task? = null,
    /**Aggregate with counts (total, completed, active)*/
    val counts: TaskCounts? = null,
    /**Current filter by completion status.*/
    val filterCompleted: Boolean? = null,
    /**Error message displayed in the UI.*/
    val error: String? = null,
)
