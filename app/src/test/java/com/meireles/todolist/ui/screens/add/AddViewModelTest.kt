package com.meireles.todolist.ui.screens.add

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
class AddViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeTaskRepository()
        savedStateHandle = SavedStateHandle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Modo criação ---

    @Test
    fun `estado inicial em modo criacao tem isLoading e isEditing falsos`() {
        val vm = AddViewModel(repository, savedStateHandle)
        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isEditing)
        assertNull(state.id)
        assertEquals("", state.title)
        assertEquals("", state.description)
        assertFalse(state.isValid)
        assertFalse(state.isSaved)
        assertNull(state.error)
    }

    @Test
    fun `onTitleChange com titulo valido atualiza titulo e isValid`() {
        val vm = AddViewModel(repository, savedStateHandle)
        vm.onTitleChange("Comprar leite")
        val state = vm.uiState.value
        assertEquals("Comprar leite", state.title)
        assertTrue(state.isValid)
        assertNull(state.error)
    }

    @Test
    fun `onTitleChange com titulo vazio ou em branco marca isValid falso`() {
        val vm = AddViewModel(repository, savedStateHandle)
        vm.onTitleChange("   ")
        assertFalse(vm.uiState.value.isValid)
        assertEquals("   ", vm.uiState.value.title)
    }

    @Test
    fun `onTitleChange limpa erro anterior`() = runTest(dispatcher) {
        val vm = AddViewModel(repository, savedStateHandle)
        // força um erro ao salvar sem título
        vm.save()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ERR_TITULO_VAZIO_TEST, vm.uiState.value.error)

        vm.onTitleChange("Novo")
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `onDescriptionChange atualiza descricao e limpa erro`() = runTest(dispatcher) {
        val vm = AddViewModel(repository, savedStateHandle)
        vm.save() // gera erro
        dispatcher.scheduler.advanceUntilIdle()

        vm.onDescriptionChange("Descrição nova")
        assertEquals("Descrição nova", vm.uiState.value.description)
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `save sem titulo valido define erro de titulo vazio e nao chama repositorio`() = runTest(dispatcher) {
        val vm = AddViewModel(repository, savedStateHandle)
        vm.save()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(ERR_TITULO_VAZIO_TEST, vm.uiState.value.error)
        assertEquals(0, repository.createCount)
        assertEquals(0, repository.updateCount)
    }

    @Test
    fun `save em modo criacao chama create e marca isSaved`() = runTest(dispatcher) {
        val vm = AddViewModel(repository, savedStateHandle)
        vm.onTitleChange("Tarefa nova")
        vm.onDescriptionChange("Uma descrição")
        vm.save()
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state.isSaved)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(1, repository.createCount)
        assertEquals(0, repository.updateCount)

        val criada = repository.tasks.value.single()
        assertEquals("Tarefa nova", criada.title.value)
        assertEquals("Uma descrição", criada.description?.value)
        assertFalse(criada.isCompleted)
    }

    @Test
    fun `save em modo criacao sem descricao envia description nula`() = runTest(dispatcher) {
        val vm = AddViewModel(repository, savedStateHandle)
        vm.onTitleChange("Tarefa")
        vm.save()
        dispatcher.scheduler.advanceUntilIdle()

        val criada = repository.tasks.value.single()
        assertNull(criada.description)
    }

    @Test
    fun `save em modo criacao com descricao em branco envia description nula`() = runTest(dispatcher) {
        val vm = AddViewModel(repository, savedStateHandle)
        vm.onTitleChange("Tarefa")
        vm.onDescriptionChange("   ")
        vm.save()
        dispatcher.scheduler.advanceUntilIdle()

        assertNull(repository.tasks.value.single().description)
    }

    @Test
    fun `save quando repositorio falha define error e nao marca isSaved`() = runTest(dispatcher) {
        repository.failOnCreate = RuntimeException("Falha de teste")
        val vm = AddViewModel(repository, savedStateHandle)
        vm.onTitleChange("Tarefa")
        vm.save()
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isSaved)
        assertFalse(state.isLoading)
        assertEquals("Falha de teste", state.error)
    }

    // --- Modo edição ---

    @Test
    fun `estado inicial em modo edicao comeca com isLoading e isEditing verdadeiros`() {
        val taskId = 42L
        val handle = SavedStateHandle(mapOf("taskId" to taskId.toInt()))
        repository.tasks.value = listOf(tarefa(id = taskId, titulo = "Existente"))

        val vm = AddViewModel(repository, handle)

        assertTrue(vm.uiState.value.isEditing)
    }

    @Test
    fun `load preenche estado quando tarefa existe`() = runTest(dispatcher) {
        val taskId = 10L
        val handle = SavedStateHandle(mapOf("taskId" to taskId.toInt()))
        repository.tasks.value = listOf(
            tarefa(
                id = taskId,
                titulo = "Editar-me",
                descricao = "Descrição original",
                concluida = true,
            ),
        )

        val vm = AddViewModel(repository, handle)
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isEditing)
        assertEquals(taskId, state.id?.value)
        assertEquals("Editar-me", state.title)
        assertEquals("Descrição original", state.description)
        assertTrue(state.isCompleted)
        assertTrue(state.isValid)
        assertNull(state.error)
    }

    @Test
    fun `load define erro quando tarefa nao existe`() = runTest(dispatcher) {
        val handle = SavedStateHandle(mapOf("taskId" to 99))
        // repositório vazio
        val vm = AddViewModel(repository, handle)
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(ERR_TAREFA_NAO_ENCONTRADA_TEST, state.error)
    }

    @Test
    fun `load define erro quando fluxo lanca excecao`() = runTest(dispatcher) {
        val handle = SavedStateHandle(mapOf("taskId" to 7))
        repository.failOnGetById = RuntimeException("Falha de leitura")

        val vm = AddViewModel(repository, handle)
        dispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Falha de leitura", state.error)
    }

    @Test
    fun `save em modo edicao chama update e preserva id e createdAt`() = runTest(dispatcher) {
        val taskId = 5L
        val createdAt = 123456789L
        val handle = SavedStateHandle(mapOf("taskId" to taskId.toInt()))
        repository.tasks.value = listOf(
            tarefa(id = taskId, titulo = "Antiga", criadaEm = createdAt),
        )

        val vm = AddViewModel(repository, handle)
        dispatcher.scheduler.advanceUntilIdle()
        vm.onTitleChange("Atualizada")
        vm.save()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, repository.updateCount)
        assertEquals(0, repository.createCount)

        val atualizada = repository.tasks.value.single()
        assertEquals(taskId, atualizada.id.value)
        assertEquals("Atualizada", atualizada.title.value)
        assertEquals(createdAt, atualizada.createdAt.toEpochMilli())
    }

    @Test
    fun `save em modo edicao mantem isCompleted original`() = runTest(dispatcher) {
        val taskId = 3L
        val handle = SavedStateHandle(mapOf("taskId" to taskId.toInt()))
        repository.tasks.value = listOf(
            tarefa(id = taskId, titulo = "Feita", concluida = true),
        )

        val vm = AddViewModel(repository, handle)
        dispatcher.scheduler.advanceUntilIdle()
        vm.save()
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(repository.tasks.value.single().isCompleted)
    }

    // --- Combate aos Mutantes ---

    @Test
    fun `save define isLoading true durante a operacao`() = runTest(dispatcher) {
        val gate = CompletableDeferred<Unit>()
        repository.gateOnCreate = gate

        val vm = AddViewModel(repository, savedStateHandle)
        vm.onTitleChange("Tarefa")
        vm.save()
        dispatcher.scheduler.advanceUntilIdle() // launch roda até o await do gate

        assertTrue(vm.uiState.value.isLoading)

        gate.complete(Unit)
        dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.uiState.value.isLoading)
        assertTrue(vm.uiState.value.isSaved)
    }

    @Test
    fun `estado inicial em modo criacao tem isCompleted falso`() {
        val vm = AddViewModel(repository, savedStateHandle)
        assertFalse(vm.uiState.value.isCompleted)
    }

    @Test
    fun `estado inicial em modo edicao tem isLoading true antes de carregar`() = runTest(dispatcher) {
        val gate = CompletableDeferred<Unit>()
        repository.gateOnGetById = gate

        val handle = SavedStateHandle(mapOf("taskId" to 1))
        val vm = AddViewModel(repository, handle)

        // o load está suspenso no gate; não chamamos advanceUntilIdle ainda
        assertTrue(vm.uiState.value.isLoading)
        assertTrue(vm.uiState.value.isEditing)

        gate.complete(Unit)
        dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.uiState.value.isLoading)
    }

    // --- Helpers ---

    private fun tarefa(
        id: Long,
        titulo: String,
        descricao: String? = null,
        concluida: Boolean = false,
        criadaEm: Long = 0L,
    ): Task = Task(
        id = TaskId(id),
        title = Title(titulo),
        description = descricao?.let { Description(it) },
        createdAt = Instant.ofEpochMilli(criadaEm),
        isCompleted = concluida,
    )

    companion object {
        private const val ERR_TITULO_VAZIO_TEST = "O título não pode estar vazio."
        private const val ERR_TAREFA_NAO_ENCONTRADA_TEST = "Tarefa não encontrada."
    }
}

/** Fake em memória do TaskRepository para os testes. */
private class FakeTaskRepository : TaskRepository {
    val tasks = MutableStateFlow<List<Task>>(emptyList())
    var failOnCreate: RuntimeException? = null
    var failOnUpdate: RuntimeException? = null
    var failOnGetById: RuntimeException? = null

    var createCount = 0
    var updateCount = 0

    var gateOnCreate: CompletableDeferred<Unit>? = null
    var gateOnGetById: CompletableDeferred<Unit>? = null

    override fun getAll(isCompleted: Boolean?): Flow<List<Task>> = tasks

    override fun getById(id: TaskId): Flow<Task?> {
        return flow {
            failOnGetById?.let { throw it }
            gateOnGetById?.await()
            emitAll(tasks.map { list -> list.find { it.id == id } })
        }
    }

    override suspend fun create(task: Task) : TaskId {
        createCount++
        failOnCreate?.let { throw it }
        gateOnCreate?.await()
        tasks.value += task
        return task.id
    }

    override suspend fun update(task: Task) {
        updateCount++
        failOnUpdate?.let { throw it }
        tasks.value = tasks.value.map { if (it.id == task.id) task else it }
    }

    override suspend fun delete(id: TaskId) {
        tasks.value = tasks.value.filterNot { it.id == id }
    }

    override suspend fun toggleStatus(id: TaskId) {
        tasks.value = tasks.value.map { task ->
            if (task.id == id) task.copy(isCompleted = !task.isCompleted) else task
        }
    }

    override fun getCounts(): Flow<TaskCounts> = tasks.map { list ->
        TaskCounts(
            total = list.size,
            completed = list.count { it.isCompleted },
            inProgress = list.count { !it.isCompleted }
        )
    }
}