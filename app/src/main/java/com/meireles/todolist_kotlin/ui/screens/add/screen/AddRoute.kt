package com.meireles.todolist_kotlin.ui.screens.add.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meireles.todolist_kotlin.ui.screens.add.AddViewModel

/**
 *Screen path for creating/editing tasks.
 *
 * If 'taskId' is null: create mode.
 * If 'taskId' has value: edit mode.
 * */
@Composable
fun AddRoute(
    onSavedNavigateBack: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AddViewModel = hiltViewModel()
) {
    /**
     *Collects the state exposed by the ViewModel.
     * */
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    // Upon successful saving, navigate back
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSavedNavigateBack()
    }

    // Renders the Add/Edit screen, delegating UI intents to the ViewModel.
    AddScreen(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onSave = viewModel::save,
        onCancel = onCancel,
    )

}