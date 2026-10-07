package com.meireles.todolist.di

import com.meireles.todolist.data.repositories.TaskRepositoryImpl
import com.meireles.todolist.domain.repositories.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt responsável por ligar as abstrações da camada de domínio
 * às suas implementações concretas na camada de dados.
 *
 * As dependências são registradas no [SingletonComponent], garantindo
 * que a mesma instância de repositório seja reutilizada durante todo o
 * ciclo de vida da aplicação.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    /**
     * Informa ao Hilt que, quando alguém solicitar [TaskRepository],
     * deve injetar uma instância de [TaskRepositoryImpl].
     */
    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
}
