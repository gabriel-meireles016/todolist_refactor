package com.meireles.todolist_kotlin.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

/** Nome do arquivo de banco de dados SQLite usado pelo app. */
const val TODO_DATABASE_NAME = "todolist_kt"

/** Versão atual do schema do banco. Incrementar a cada mudança estrutural. */
private const val DATABASE_VERSION = 1

/**
 * Configuração do banco de dados Room do aplicativo.
 *
 * Declara as entidades persistidas e expõe os DAOs disponíveis.
 * A versão do schema é controlada por [DATABASE_VERSION] — deve ser
 * incrementada sempre que a estrutura das entidades mudar.
 */
@Database(
    entities = [TaskEntity::class],
    version = DATABASE_VERSION,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    /** Retorna o DAO responsável pelo acesso à tabela de tarefas. */
    abstract fun taskDao(): TaskDao
}
