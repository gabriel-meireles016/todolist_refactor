package com.meireles.todolist_kotlin.ui.screens.add.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meireles.todolist_kotlin.ui.components.AppBarPattern
import com.meireles.todolist_kotlin.ui.screens.add.AddUiState
import com.meireles.todolist_kotlin.ui.theme.deepPurple400

/**Main screen for adding and editing.*/
@Composable
fun AddScreen(
    state: AddUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBarPattern(backScreen = 1, onCancel = { onCancel() }) },
    ) { innerPadding ->

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(innerPadding).padding(16.dp))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onTitleChange,
                label = { Text(text = "Título") },
                isError = !state.isValid && state.title.isBlank(),
                supportingText = {
                    if (!state.isValid && state.title.isBlank()) Text("Título é Obrigatório")
                }
            )

            OutlinedTextField(
                value = state.description,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onDescriptionChange,
                label = { Text("Descrição") }
            )

            ElevatedButton(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color.White,
                    contentColor = deepPurple400
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 8.dp
                ),
                enabled = state.isValid && !state.isLoading
            ) {
                Text(if (state.isEditing) "Editar" else "Adicionar")
            }

            if (state.error != null) {
                Text(text = state.error, color = Color.Red)
            }

        }

    }
}