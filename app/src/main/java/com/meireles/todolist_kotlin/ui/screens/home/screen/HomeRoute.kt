package com.meireles.todolist_kotlin.ui.screens.home.screen

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meireles.todolist_kotlin.ui.screens.home.HomeViewModel

/**
 * Entry point of the HomeScreen in the navigation graph.
 * */
@Composable
fun HomeRoute(
    goAdd: () -> Unit,
    goEdit: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    /**Collects the state exposed by the ViewModel.*/
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    // Renders the HomeScreen, injecting state and linking UI intentions on VM actions.
    HomeScreen(
        state = uiState,
        goAdd = goAdd,
        goEdit = goEdit,
        onToggleComplete = { id -> viewModel.toggleStatus(id) },
        onDelete = { id -> viewModel.delete(id) },
        onFilterChange = { completed -> viewModel.setFilter(completed) },
    )
}