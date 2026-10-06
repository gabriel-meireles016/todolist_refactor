package com.meireles.todolist_kotlin.ui.screens.home.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meireles.todolist_kotlin.domain.model.TaskId
import com.meireles.todolist_kotlin.ui.screens.home.HomeViewModel

/**
 * Ponto de entrada da `HomeScreen` no grafo de navegação.
 *
 * Coleta o estado exposto pelo [HomeViewModel] e o injeta na `HomeScreen`,
 * conectando as intenções da UI às ações do ViewModel.
 *
 * @param goAdd callback de navegação para a tela de adição.
 * @param goEdit callback de navegação para a tela de edição.
 * @param viewModel ViewModel injetado pelo Hilt.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeRoute(
    goAdd: () -> Unit,
    goEdit: (TaskId) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
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
