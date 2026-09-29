package com.meireles.todolist_kotlin.ui.screens.home.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.meireles.todolist_kotlin.domain.model.Task
import com.meireles.todolist_kotlin.domain.model.TaskId
import com.meireles.todolist_kotlin.ui.components.AddButton
import com.meireles.todolist_kotlin.ui.components.AppBarPattern
import com.meireles.todolist_kotlin.ui.components.TaskFilter
import com.meireles.todolist_kotlin.ui.components.next
import com.meireles.todolist_kotlin.ui.components.toBooleanOrNull
import com.meireles.todolist_kotlin.ui.screens.home.HomeUiState
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

/** Valor sentinela para "nenhuma tarefa selecionada". */
private const val ID_NENHUM_SELECIONADO = -1

/** Padding vertical da lista de tarefas. */
private val PADDING_LISTA_VERTICAL = 16.dp

/** Padding horizontal dos itens da lista. */
private val PADDING_ITEM_HORIZONTAL = 16.dp

/** Padding vertical dos itens da lista. */
private val PADDING_ITEM_VERTICAL = 8.dp

/** Padding interno do card de tarefa. */
private val PADDING_CARD = 16.dp

/** Espaçamento entre o ícone de toggle e o conteúdo do card. */
private val ESPACAMENTO_ICONE_CONTEUDO = 16.dp

/** Elevação padrão dos cards de tarefa. */
private val ELEVACAO_CARD = 6.dp

/** Padding vertical dos itens do bottom sheet. */
private val PADDING_ITEM_SHEET = 4.dp

/** Formato de data padrão usado na UI. */
private const val FORMATO_DATA_PADRAO = "dd/MM/yyyy"

/** Valor exibido quando a data não pode ser formatada. */
private const val DATA_INVALIDA = "-"

/**
 * Tela principal do app.
 *
 * @param goAdd callback de navegação para a tela de adição.
 * @param goEdit callback de navegação para a tela de edição.
 * @param state estado atual da tela.
 * @param onToggleComplete callback para alternar o status de conclusão.
 * @param onDelete callback para deletar uma tarefa.
 * @param onFilterChange callback para alterar o filtro.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    goAdd: () -> Unit,
    goEdit: (TaskId) -> Unit,
    state: HomeUiState,
    onToggleComplete: (TaskId) -> Unit,
    onDelete: (TaskId) -> Unit,
    onFilterChange: (Boolean?) -> Unit
) {

    var showSheet by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf(TaskId(ID_NENHUM_SELECIONADO.toLong())) }
    var searchVisible by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.PartiallyExpanded }
    )

    var currentFilter by rememberSaveable { mutableStateOf(TaskFilter.ALL) }
    val cycleFilter: () -> Unit = {
        currentFilter = currentFilter.next()
        onFilterChange(currentFilter.toBooleanOrNull())
    }

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
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
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
                onSearch = { searchVisible = true }
            )
        }
    ) { innerPadding ->
        val filteredTasks: List<Task> = remember(state.tasks, searchText.text) {
            filterTasks(state.tasks, searchText.text)
        }

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(vertical = PADDING_LISTA_VERTICAL),
        ) {
            item {
                SearchBar(
                    visible = searchVisible,
                    query = searchText,
                    onQueryChange = { searchText = it },
                    onClose = { searchVisible = false }
                )
            }

            if (filteredTasks.isEmpty()) {
                item { EmptyState() }
            } else {
                items(items = filteredTasks, key = { it.id.value }) { task ->
                    TaskCard(
                        task = task,
                        onLongPress = {
                            selectedId = task.id
                            showSheet = true
                        },
                        onToggle = { onToggleComplete(task.id) }
                    )
                }
            }
        }

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

/** Filtra tarefas pelo texto de busca (título, descrição ou ID). */
private fun filterTasks(tasks: List<Task>, query: String): List<Task> {
    if (query.isBlank()) return tasks
    val q = query.trim()
    return tasks.filter { task ->
        task.title.value.contains(q, ignoreCase = true) ||
                (task.description?.value?.contains(q, ignoreCase = true) == true) ||
                task.id.value.toString() == q
    }
}

@Composable
private fun SearchBar(
    visible: Boolean,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PADDING_ITEM_HORIZONTAL),
            value = query,
            label = { Text(text = "Buscar...") },
            onValueChange = onQueryChange,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar busca"
                    )
                }
            }
        )
    }
}

/**
 * Card de tarefa com título, ID, descrição (se houver) e data de criação.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TaskCard(
    task: Task,
    onLongPress: () -> Unit,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = PADDING_ITEM_HORIZONTAL,
                vertical = PADDING_ITEM_VERTICAL
            )
            .combinedClickable(
                onClick = {},
                onLongClick = onLongPress
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = ELEVACAO_CARD)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = PADDING_CARD)
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.padding(end = ESPACAMENTO_ICONE_CONTEUDO)
            ) {
                Icon(
                    imageVector = if (task.isCompleted) {
                        Icons.Outlined.CheckCircle
                    } else {
                        Icons.Outlined.Circle
                    },
                    contentDescription = if (task.isCompleted) {
                        "Tarefa concluída"
                    } else {
                        "Tarefa em andamento"
                    }
                )
            }

            Column {
                Text(
                    text = task.title.value,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "ID: ${task.id.value}",
                    style = MaterialTheme.typography.bodyMedium
                )

                task.description?.let {
                    Text(
                        text = "Descrição: ${it.value}",
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

/** Estado vazio exibido quando não há tarefas para mostrar. */
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(PADDING_ITEM_HORIZONTAL),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nenhuma tarefa encontrada.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Bottom sheet com ações aplicáveis à tarefa selecionada.
 */
@Composable
private fun ActionsSheet(
    selectedId: TaskId,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = PADDING_ITEM_HORIZONTAL,
                vertical = PADDING_ITEM_VERTICAL
            )
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = PADDING_CARD),
            text = "Ações para o item #${selectedId.value}",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        ActionItem(
            label = "Editar",
            icon = Icons.Default.Edit,
            onClick = onEdit
        )

        ActionItem(
            label = "Excluir",
            icon = Icons.Default.Remove,
            onClick = onDelete
        )
    }
}

@Composable
private fun ActionItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PADDING_ITEM_SHEET)
            .clickable(onClick = onClick),
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null)
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Converte um [Instant] para string no formato [pattern].
 *
 * @param pattern formato de data (padrão: `dd/MM/yyyy`).
 * @return a data formatada, ou [DATA_INVALIDA] se a conversão falhar.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Instant.formatAsDate(pattern: String = FORMATO_DATA_PADRAO): String =
    runCatching {
        SimpleDateFormat(pattern, Locale.getDefault()).format(Date.from(this))
    }.getOrDefault(DATA_INVALIDA)