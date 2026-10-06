package com.meireles.todolist_kotlin.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Padding interno do botão de voltar. */
private val PADDING_BOTAO_VOLTAR = 8.dp

/**
 * Componente reutilizável de seta de voltar.
 *
 * @param onBackClick callback acionado ao clicar na seta.
 */
@Composable
fun BackButton(onBackClick: () -> Unit = {}) {
    IconButton(
        modifier = Modifier.padding(all = PADDING_BOTAO_VOLTAR),
        onClick = onBackClick,
        colors =
            IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
            ),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar",
        )
    }
}
