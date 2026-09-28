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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meireles.todolist_kotlin.data.repositories.TaskCounts
import com.meireles.todolist_kotlin.ui.theme.deepPurple400
import com.meireles.todolist_kotlin.ui.theme.deepPurple500

/**
 * Top bar with title, back button and a filter/counter.
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarPattern(
    backScreen: Int? = null,
    onCancel: () -> Unit = {},
    counts: TaskCounts? = null,
    currentFilter: TaskFilter = TaskFilter.ALL,
    onCycleFilter: () -> Unit = {},
    title: String = "TO DO LIST KOLTIN"
) {

    /**Defines the label and value displayed on the chip based on the active filter.*/
    val (label, value) = when (currentFilter) {
        TaskFilter.ALL -> "Total" to (counts?.total ?: 0)
        TaskFilter.ACTIVE -> "Ativos" to (counts?.inProgress ?: 0)
        TaskFilter.COMPLETED -> "Concluídos" to (counts?.completed ?: 0)
    }

    CenterAlignedTopAppBar(
        colors = topAppBarColors(
            containerColor = deepPurple400,
            titleContentColor = Color.White
        ),
        title = {
            Text(title)
        },
        navigationIcon = {
            if (backScreen == 1) {
                BackButton(onCancel = onCancel)
            }
        },
        actions = {

            if (backScreen == null) {
                // Chip based on the active filter.
                Box(
                    modifier = Modifier
                        .background(deepPurple500, shape = CircleShape)
                        .padding(5.dp)
                        .width(90.dp)
                        .clickable { onCycleFilter() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = label,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .background(color = deepPurple400, shape = CircleShape)
                                .padding(6.dp)
                                .size(16.dp)
                        ) {
                            Text(
                                text = value.toString(),
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

        }
    )
}