package com.meireles.todolist_kotlin.ui.screens.add.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meireles.todolist_kotlin.ui.screens.add.AddViewModel

/**
 * Ponto de entrada da `AddScreen` no grafo de navegação.
 *
 * Em modo criação (`taskId` nulo): o formulário inicia vazio.
 * Em modo edição (`taskId` preenchido): o formulário é preenchido com os
 * dados da tarefa.
 *
 * @param onSavedNavigateBack callback acionado após salvar com sucesso.
 * @param onBackClick callback do botão de voltar.
 * @param viewModel ViewModel injetado pelo Hilt.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddRoute(
    onSavedNavigateBack: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSavedNavigateBack()
    }

    AddScreen(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onSave = viewModel::save,
        onBackClick = onBackClick
    )
}