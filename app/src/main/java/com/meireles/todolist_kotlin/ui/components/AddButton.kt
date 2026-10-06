package com.meireles.todolist_kotlin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

/** Ângulo de rotação do ícone principal quando o menu está aberto. */
private const val ANGULO_ROTACAO_ABERTO = 45f

/** Espaçamento vertical entre os FABs do menu. */
private val ESPACAMENTO_ENTRE_FABS = 8.dp

/** Espaçamento horizontal entre o rótulo e o FAB. */
private val ESPACAMENTO_ROTULO_FAB = 8.dp

/** Padding horizontal do rótulo. */
private val PADDING_ROTULO_HORIZONTAL = 12.dp

/** Padding vertical do rótulo. */
private val PADDING_ROTULO_VERTICAL = 6.dp

/** Rótulo da animação de rotação do FAB. */
private const val LABEL_ANIMACAO_ROTACAO = "fabRotation"

/**
 * FAB principal com menu "speed dial".
 *
 * Quando aberto: exibe ações secundárias (Adicionar, Buscar).
 * Quando fechado: exibe apenas o FAB com ícone '+'.
 *
 * @param onSearch callback da ação "Buscar".
 * @param goAdd callback da ação "Adicionar" (navegação).
 */
@Composable
fun AddButton(
    onSearch: () -> Unit = {},
    goAdd: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) ANGULO_ROTACAO_ABERTO else 0f,
        label = LABEL_ANIMACAO_ROTACAO,
    )

    Box(contentAlignment = Alignment.BottomEnd) {
        // Área invisível que fecha o menu ao clicar fora.
        AnimatedVisibility(visible = expanded) {
            Box(
                modifier =
                    Modifier
                        .clickable { expanded = false },
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(ESPACAMENTO_ENTRE_FABS),
        ) {
            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(ESPACAMENTO_ENTRE_FABS),
                    horizontalAlignment = Alignment.End,
                ) {
                    LabeledSmallFab(
                        label = "Adicionar",
                        icon = {
                            Icon(imageVector = Icons.Outlined.Task, contentDescription = null)
                        },
                        onClick = {
                            expanded = false
                            goAdd()
                        },
                    )

                    LabeledSmallFab(
                        label = "Buscar",
                        icon = {
                            Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                        },
                        onClick = {
                            onSearch()
                            expanded = false
                        },
                    )
                }
            }

            FloatingActionButton(
                onClick = { expanded = !expanded },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation),
                )
            }
        }
    }
}

/**
 * Componente reutilizável "rótulo + mini FAB".
 *
 * @param label texto exibido ao lado do FAB.
 * @param icon ícone do FAB.
 * @param onClick callback de clique.
 */
@Composable
fun LabeledSmallFab(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ESPACAMENTO_ROTULO_FAB),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge,
            modifier =
                Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    ).padding(
                        horizontal = PADDING_ROTULO_HORIZONTAL,
                        vertical = PADDING_ROTULO_VERTICAL,
                    ),
        )

        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
        ) {
            icon()
        }
    }
}
