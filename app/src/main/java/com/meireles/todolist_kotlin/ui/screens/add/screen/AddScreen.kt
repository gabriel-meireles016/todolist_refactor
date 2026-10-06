package com.meireles.todolist_kotlin.ui.screens.add.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meireles.todolist_kotlin.ui.components.AppBarPattern
import com.meireles.todolist_kotlin.ui.screens.add.AddUiState

/** Padding vertical entre os elementos do formulário. */
private val PADDING_ENTRE_CAMPOS = 16.dp

/** Padding interno da tela. */
private val PADDING_TELA = 16.dp

/** Padding vertical do botão de salvar. */
private val PADDING_BOTAO_SALVAR = 8.dp

/** Elevação do botão de salvar. */
private val ELEVACAO_BOTAO_SALVAR = 8.dp

/**
 * Tela principal de Adição/Edição de tarefa.
 *
 * @param state estado atual do formulário.
 * @param onTitleChange callback de alteração do título.
 * @param onDescriptionChange callback de alteração da descrição.
 * @param onSave callback de salvamento.
 * @param onBackClick callback do botão de voltar.
 */
@Composable
fun AddScreen(
    state: AddUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBarPattern(
                showBackButton = true,
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(PADDING_TELA),
            )
            return@Scaffold
        }

        FormularioTarefa(
            state = state,
            onTitleChange = onTitleChange,
            onDescriptionChange = onDescriptionChange,
            onSave = onSave,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

/**
 * Formulário de edição/criação de tarefa.
 */
@Composable
private fun FormularioTarefa(
    state: AddUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tituloInvalido = !state.isValid && state.title.isBlank()

    Column(
        modifier =
            modifier
                .padding(horizontal = PADDING_TELA, vertical = PADDING_TELA),
        verticalArrangement = Arrangement.spacedBy(PADDING_ENTRE_CAMPOS),
    ) {
        OutlinedTextField(
            value = state.title,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onTitleChange,
            label = { Text(text = "Título") },
            isError = tituloInvalido,
            supportingText = {
                if (tituloInvalido) {
                    Text("Título é obrigatório")
                }
            },
        )

        OutlinedTextField(
            value = state.description,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onDescriptionChange,
            label = { Text("Descrição") },
        )

        ElevatedButton(
            onClick = onSave,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = PADDING_BOTAO_SALVAR),
            colors =
                ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            elevation =
                ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = ELEVACAO_BOTAO_SALVAR,
                ),
            enabled = state.isValid && !state.isLoading,
        ) {
            Text(if (state.isEditing) "Editar" else "Adicionar")
        }

        state.error?.let { mensagem ->
            Text(
                text = mensagem,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
