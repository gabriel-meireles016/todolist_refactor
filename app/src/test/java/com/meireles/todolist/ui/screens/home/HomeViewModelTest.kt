package com.meireles.todolist.ui.screens.home

import com.meireles.todolist.data.repositories.TaskCounts
import com.meireles.todolist.domain.model.Description
import com.meireles.todolist.domain.model.Task
import com.meireles.todolist.domain.model.TaskId
import com.meireles.todolist.domain.model.Title
import com.meireles.todolist.domain.repositories.TaskRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeTaskRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Estado inicial e observação de tarefas ---

    @Test
    fun `estado inicial comeca com isLoading true`() {
        val vm = HomeViewModel(repository)
        assertTrue(vm.uiState.value.isLoading)
        assertEquals(emptyList<Task>(), vm.uiState.value.tasks)
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `observeTasks preenche lista e desliga isLoading`() = runTest(dispatcher) {
        repository.tasks.value = listOf(tarefa(1), tarefa(2))
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertEquals(2, vm.uiState.value.tasks.size)
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `observeTasks define erro quando fluxo lanca excecao`() = runTest(dispatcher) {
        repository.emitCounts = false
        repository.failOnGetAll = RuntimeException("Falha de leitura")
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertEquals("Falha de leitura", vm.uiState.value.error)
    }

    @Test
    fun `observeTasks define mensagem padrao quando excecao nao tem message`() = runTest(dispatcher) {
        repository.emitCounts = false
        repository.failOnGetAll = RuntimeException()
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Erro ao carregar tarefas", vm.uiState.value.error)
    }

    // --- Contagens ---

    @Test
    fun `observeCounts preenche counts ao carregar`() = runTest(dispatcher) {
        repository.tasks.value = listOf(
            tarefa(1, concluida = true),
            tarefa(2, concluida = false),
            tarefa(3, concluida = false),
        )
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        val counts = vm.uiState.value.counts
        assertEquals(3, counts?.total)
        assertEquals(1, counts?.completed)
        assertEquals(2, counts?.inProgress)
    }

    @Test
    fun `observeCounts define erro quando fluxo de counts falha`() = runTest(dispatcher) {
        repository.failOnGetCounts = RuntimeException("Falha counts")
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Falha counts", vm.uiState.value.error)
    }

    @Test
    fun `observeCounts define mensagem padrao quando excecao nao tem message`() = runTest(dispatcher) {
        repository.failOnGetCounts = RuntimeException()
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Erro ao carregar tarefas", vm.uiState.value.error)
    }

    // --- Filtro ---

    @Test
    fun `setFilter altera filtro e recria observacao de tarefas`() = runTest(dispatcher) {
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        vm.setFilter(true)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, vm.uiState.value.filterCompleted)
        // O FakeTaskRepository deve registrar o último filtro solicitado
        assertEquals(true, repository.lastFilter)
    }

    @Test
    fun `setFilter define isLoading true antes de recarregar`() = runTest(dispatcher) {
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        // bloqueia a próxima coleta para ver o estado intermediário
        repository.gateOnGetAll = CompletableDeferred()
        vm.setFilter(false)
        // não avança o scheduler: observeTasks acabou de ser lançado e está suspenso
        assertTrue(vm.uiState.value.isLoading)
        assertEquals(false, vm.uiState.value.filterCompleted)

        repository.gateOnGetAll?.complete(Unit)
        dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `setFilter com null volta a observar todas as tarefas`() = runTest(dispatcher) {
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        vm.setFilter(true)
        dispatcher.scheduler.advanceUntilIdle()
        vm.setFilter(null)
        dispatcher.scheduler.advanceUntilIdle()

        assertNull(vm.uiState.value.filterCompleted)
        assertNull(repository.lastFilter)
    }

    // --- delete ---

    @Test
    fun `delete remove tarefa com sucesso`() = runTest(dispatcher) {
        repository.tasks.value = listOf(tarefa(1), tarefa(2))
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        vm.delete(TaskId(1))
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(2L), repository.tasks.value.map { it.id.value })
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `delete define erro quando repositorio falha`() = runTest(dispatcher) {
        repository.failOnDelete = RuntimeException("Falha ao deletar")
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        vm.delete(TaskId(1))
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Falha ao deletar", vm.uiState.value.error)
    }

    @Test
    fun `delete define mensagem padrao quando excecao nao tem message`() = runTest(dispatcher) {
        repository.failOnDelete = RuntimeException()
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        vm.delete(TaskId(1))
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Erro ao carregar tarefas", vm.uiState.value.error)
    }

    // --- toggleStatus ---

    @Test
    fun `toggleStatus alterna conclusao da tarefa`() = runTest(dispatcher) {
        repository.tasks.value = listOf(tarefa(1, concluida = false))
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        vm.toggleStatus(TaskId(1))
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(repository.tasks.value.single().isCompleted)
    }

    @Test
    fun `toggleStatus define erro quando repositorio falha`() = runTest(dispatcher) {
        repository.failOnToggleStatus = RuntimeException("Falha ao alternar")
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        vm.toggleStatus(TaskId(1))
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Falha ao alternar", vm.uiState.value.error)
    }

    @Test
    fun `toggleStatus define mensagem padrao quando excecao nao tem message`() = runTest(dispatcher) {
        repository.failOnToggleStatus = RuntimeException()
        val vm = HomeViewModel(repository)
        dispatcher.scheduler.advanceUntilIdle()

        vm.toggleStatus(TaskId(1))
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Erro ao carregar tarefas", vm.uiState.value.error)
    }

    // --- Helpers ---

    private fun tarefa(
        id: Long,
        concluida: Boolean = false,
        criadaEm: Long = 0L,
    ): Task = Task(
        id = TaskId(id),
        title = Title("Tarefa $id"),
        description = null,
        createdAt = Instant.ofEpochMilli(criadaEm),
        isCompleted = concluida,
    )

    private fun tarefa(
        id: Long,
        titulo: String,
        descricao: String? = null,
        concluida: Boolean = false,
        criadaEm: Long = 0L,
    ): Task = Task(
        id = TaskId(id),
        title = Title(titulo),
        description = descricao?.let(::Description),
        createdAt = Instant.ofEpochMilli(criadaEm),
        isCompleted = concluida,
    )
}

/**
 * Fake em memória do TaskRepository para os testes do HomeViewModel.
 * Estende a versão usada no AddViewModel com suporte a filtro, counts,
 * delete, toggleStatus e gates de suspensão.
 */
private class FakeTaskRepository : TaskRepository {
    val tasks = MutableStateFlow<List<Task>>(emptyList())

    var failOnGetAll: RuntimeException? = null
    var failOnGetCounts: RuntimeException? = null
    var failOnDelete: RuntimeException? = null
    var failOnToggleStatus: RuntimeException? = null

    var gateOnGetAll: CompletableDeferred<Unit>? = null

    /** Último filtro recebido em getAll; útil para verificar setFilter. */
    var lastFilter: Boolean? = null

    override fun getAll(isCompleted: Boolean?): Flow<List<Task>> {
        lastFilter = isCompleted
        return flow {
            failOnGetAll?.let { throw it }
            gateOnGetAll?.await()
            emitAll(
                tasks.map { list ->
                    list.filter {
                        isCompleted == null || it.isCompleted == isCompleted
                    }
                }
            )
        }
    }

    override fun getById(id: TaskId): Flow<Task?> =
        tasks.map { list -> list.find { it.id == id } }

    override suspend fun create(task: Task): TaskId {
        tasks.value += task
        return task.id
    }

    override suspend fun update(task: Task) {
        tasks.value = tasks.value.map { if (it.id == task.id) task else it }
    }

    override suspend fun delete(id: TaskId) {
        failOnDelete?.let { throw it }
        tasks.value = tasks.value.filterNot { it.id == id }
    }

    override suspend fun toggleStatus(id: TaskId) {
        failOnToggleStatus?.let { throw it }
        tasks.value = tasks.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    var emitCounts: Boolean = true

    override fun getCounts(): Flow<TaskCounts> {
        if (!emitCounts) return flow { }
        val failing = failOnGetCounts
        if (failing != null) return flow { throw failing }
        return tasks.map { list ->
            TaskCounts(
                total = list.size,
                completed = list.count { it.isCompleted },
                inProgress = list.count { !it.isCompleted },
            )
        }
    }
}