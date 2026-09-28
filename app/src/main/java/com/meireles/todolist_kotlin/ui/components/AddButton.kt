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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meireles.todolist_kotlin.ui.theme.deepPurple400

/**
 * Main Floating Action Button (FAB) with "speed dial" menu.
 *
 * When open: Displays three secondary actions (Add, Search).
 *
 * When closed:  Shows only the FAB with a '+' icon.
 * */
@Composable
fun AddButton(
    /**Callback to 'Search' action.*/
    onSearch: () -> Unit = {},
    /**Route to 'Add' action/screen.*/
    goAdd: () -> Unit = {}
) {
    /**Menu expansion status*/
    var expanded by remember { mutableStateOf(false) }
    /**Animates the rotation of main icon '+' to 45 degrees when opened 'x'.*/
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        label = "fabRotation"
    )

    // Container that aligns the content in the bottom right corner.
    Box(
        modifier = Modifier, contentAlignment = Alignment.BottomEnd
    ) {
        // Invisible area that covers the screen when the menu is open,
        // allowing it to be closed by clicking outside of the actions.
        AnimatedVisibility(visible = expanded) {
            Box(
                modifier = Modifier
                    .clickable { expanded = false }
            )
        }

        // Stacking column: main FAB and secondary actions menu
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {

            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // Add Task
                    LabeledSmallFab(
                        label = "Adicionar",
                        icon = { Icon(imageVector = Icons.Outlined.Task, contentDescription = null) },
                        onClick = {
                            expanded = false
                            goAdd()
                        }
                    )

                    // Search Task
                    LabeledSmallFab(
                        label = "Buscar",
                        icon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
                        onClick = {
                            onSearch()
                            expanded = false
                        }
                    )

                }
            }

            FloatingActionButton(
                onClick = { expanded = !expanded },
                containerColor = deepPurple400,
                contentColor = Color.White,
                shape = CircleShape

            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Add else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }

    }
}

/**Reusable component "label + mini FAB"*/
@Composable
fun LabeledSmallFab(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .background(color = deepPurple400, shape = CircleShape)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
        FloatingActionButton(
            onClick = onClick,
            containerColor = deepPurple400,
            contentColor = Color.White,
            shape = CircleShape,
        ) {
            icon()
        }
    }
}