package com.meireles.todolist_kotlin.ui.screens.home.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.meireles.todolist_kotlin.domain.model.Task
import com.meireles.todolist_kotlin.ui.components.AddButton
import com.meireles.todolist_kotlin.ui.components.AppBarPattern
import com.meireles.todolist_kotlin.ui.components.TaskFilter
import com.meireles.todolist_kotlin.ui.components.next
import com.meireles.todolist_kotlin.ui.components.toBooleanOrNull
import com.meireles.todolist_kotlin.ui.screens.home.HomeUiState
import com.meireles.todolist_kotlin.ui.theme.deepPurple50
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**Main app screen.*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    /**Navigates to add screen.*/
    goAdd: () -> Unit,
    /**Navigates to edit screen.*/
    goEdit: (Int) -> Unit,
    /**Screen state.*/
    state: HomeUiState,
    /**Toggles task status.*/
    onToggleComplete: (Int) -> Unit,
    /**Deletes task.*/
    onDelete: (Int) -> Unit,
    /**Changes filter between null/all, false/active and true/completed.*/
    onFilterChange: (Boolean?) -> Unit
) {

    // === BottomSheet - UI local states ===
    /**Controls display of the bottom sheet. */
    var showSheet by remember { mutableStateOf(false) }
    /**ID of the item selected in the sheet.*/
    var selectedId by remember { mutableStateOf(-1) }
    /**Show/hide search bar*/
    var searchVisible by remember { mutableStateOf(false) }
    /**Search text in the search bar.*/
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    /**Bottom sheet state. skipPartiallyExpanded avoid 'semi-open' state.*/
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            newValue != SheetValue.PartiallyExpanded
        }
    )

    // Filter
    var currentFilter by rememberSaveable { mutableStateOf(TaskFilter.ALL) }
    val cycleFilter: () -> Unit = {
        currentFilter = currentFilter.next()
        onFilterChange(currentFilter.toBooleanOrNull())
    }

    // Keep local filter synchronized with filter from state (ViewModel)
    LaunchedEffect(state.filterCompleted) {
        val expected = when (state.filterCompleted) {
            null -> TaskFilter.ALL
            false -> TaskFilter.ACTIVE
            true -> TaskFilter.COMPLETED
        }
        if (currentFilter != expected) currentFilter = expected
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = deepPurple50,
        topBar = {
            AppBarPattern(
                counts = state.counts,
                currentFilter = currentFilter,
                onCycleFilter = cycleFilter
            )
        },
        floatingActionButton = {
            AddButton(
                goAdd = goAdd,
                onSearch = { searchVisible = true },
            )
        }
    ) { innerPadding ->



        // Filtering by text
        val filteredTask: List<Task> = remember(state.tasks, searchText.text) {
            if (searchText.text.isBlank()) state.tasks
            else {
                val q = searchText.text.trim()
                state.tasks.filter { t ->
                    t.title.contains(q, ignoreCase = true) ||
                            (t.description?.contains(q, ignoreCase = true) == true) ||
                            t.id.toString() == q
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(vertical = 16.dp),
        ) {
            // Search bar
            item {

                AnimatedVisibility(
                    visible = searchVisible,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        value = searchText,
                        label = { Text(text = "Buscar...") },
                        onValueChange = { searchText = it },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { searchVisible = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }

            // Cards
            if (filteredTask.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                items(
                    items = filteredTask,
                    key = { it.id }
                ) { task ->
                    TaskCard(
                        task = task,
                        onClick = {},
                        onLongPress = {
                            selectedId = task.id
                            showSheet = true
                        },
                        onToggle = { onToggleComplete(task.id) }
                    )
                }
            }

        }

        // Bottom sheet for the selected item.
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                ActionsSheet(
                    selectedId = selectedId,
                    onEdit = {
                        showSheet = false
                        goEdit(selectedId)
                    },
                    onDelete = {
                        showSheet = false
                        onDelete(selectedId)
                    }
                )
            }
        }

    }
}

/**Card with title, ID, description (if any) and creation date about a task.*/
@Composable
private fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onToggle: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress,
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = 16.dp)
        )
        {
            IconButton(onClick = onToggle, modifier = Modifier.padding(end = 16.dp)) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                )
            }

            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "ID: ${task.id}",
                    style = MaterialTheme.typography.bodyMedium
                )
                task.description?.let {
                    Text(
                        text = "Descrição: $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "Criado em: ${task.createdAt.formatAsDate()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**Displays an empty state for the task list when there are no items to show.*/
@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Text(text = "Nenhuma tarefa encontrada.", style = MaterialTheme.typography.bodyMedium)
    }
}

/**Bottom sheet with actions applicable to the selected item in the task list.*/
@Composable
fun ActionsSheet(
    modifier: Modifier = Modifier,
    selectedId: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            text = "Ações para o item #$selectedId",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        ListItem(
            headlineContent = { Text("Editar") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .combinedClickable(onClick = onEdit),
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.White
            )
        )


        ListItem(
            headlineContent = { Text("Excluir") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .combinedClickable(onClick = onDelete),
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.White
            )
        )
    }
}


/**Conversion date*/
private fun Long.formatAsDate(pattern: String = "dd/MM/yyyy"): String {
    return runCatching {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.format(Date(this))
    }.getOrDefault("-")
}