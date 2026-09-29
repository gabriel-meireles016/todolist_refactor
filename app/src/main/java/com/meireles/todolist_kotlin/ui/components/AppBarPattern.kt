package com.meireles.todolist_kotlin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.meireles.todolist_kotlin.data.repositories.TaskCounts

/** Largura fixa do chip de filtro. */
private val LARGURA_CHIP_FILTRO = 90.dp

/** Tamanho do círculo do contador. */
private val TAMANHO_CIRCULO_CONTADOR = 16.dp

/** Espaçamento interno do chip. */
private val PADDING_CHIP = 5.dp

/** Espaçamento interno do contador. */
private val PADDING_CONTADOR = 6.dp

/** Espaçamento à esquerda do rótulo do filtro. */
private val PADDING_ROTULO_FILTRO = 4.dp

/**
 * Barra superior do app com título, botão de voltar opcional e chip
 * de filtro/contador.
 *
 * @param showBackButton `true` para exibir o botão de voltar.
 * @param onBackClick callback do botão de voltar.
 * @param counts contagens de tarefas (total, concluídas, em andamento).
 * @param currentFilter filtro atualmente ativo.
 * @param onCycleFilter callback para alternar o filtro.
 * @param title título exibido na barra.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarPattern(
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    counts: TaskCounts? = null,
    currentFilter: TaskFilter = TaskFilter.ALL,
    onCycleFilter: () -> Unit = {},
    title: String = "TO DO LIST KOTLIN"
) {

    val (label, value) = when (currentFilter) {
        TaskFilter.ALL -> "Total" to (counts?.total ?: 0)
        TaskFilter.ACTIVE -> "Ativos" to (counts?.inProgress ?: 0)
        TaskFilter.COMPLETED -> "Concluídos" to (counts?.completed ?: 0)
    }

    CenterAlignedTopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Text(title)
        },
        navigationIcon = {
            if (showBackButton) {
                BackButton(onCancel = onBackClick)
            }
        },
        actions = {
            if (!showBackButton) {
                FiltroChip(
                    label = label,
                    value = value,
                    onClick = onCycleFilter
                )
            }
        }
    )
}

/**
 * Chip clicável que exibe um rótulo de filtro e um contador.
 *
 * @param label rótulo do filtro (ex: "Total", "Ativos").
 * @param value valor numérico exibido no contador.
 * @param onClick callback de clique.
 */
@Composable
private fun FiltroChip(
    label: String,
    value: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
            .padding(PADDING_CHIP)
            .width(LARGURA_CHIP_FILTRO)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = PADDING_ROTULO_FILTRO)
            )
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .padding(PADDING_CONTADOR)
                    .size(TAMANHO_CIRCULO_CONTADOR)
            ) {
                Text(
                    text = value.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
