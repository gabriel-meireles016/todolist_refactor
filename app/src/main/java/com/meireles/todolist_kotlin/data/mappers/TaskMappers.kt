package com.meireles.todolist_kotlin.data.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.meireles.todolist_kotlin.data.database.TaskEntity
import com.meireles.todolist_kotlin.domain.model.Description
import com.meireles.todolist_kotlin.domain.model.Task
import com.meireles.todolist_kotlin.domain.model.TaskId
import com.meireles.todolist_kotlin.domain.model.Title
import java.time.Instant

/**
 * Converte a entidade de persistência [TaskEntity] para o modelo de domínio [Task].
 *
 * O `createdAt` é traduzido de milissegundos (formato do SQLite/Room)
 * para [Instant] (formato do domínio).
 */
@RequiresApi(Build.VERSION_CODES.O)
fun TaskEntity.toDomain(): Task =
    Task(
        id = TaskId(id.toLong()),
        title = Title(title),
        description = description?.let(::Description),
        createdAt = Instant.ofEpochMilli(createdAt),
        isCompleted = isCompleted,
    )

/**
 * Converte o modelo de domínio [Task] para a entidade de persistência [TaskEntity].
 *
 * O `createdAt` é traduzido de [Instant] para milissegundos desde a epoch
 * (formato aceito pelo Room).
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Task.toEntity(): TaskEntity =
    TaskEntity(
        id = id.value.toInt(),
        title = title.value,
        description = description?.value,
        createdAt = createdAt.toEpochMilli(),
        isCompleted = isCompleted,
    )
