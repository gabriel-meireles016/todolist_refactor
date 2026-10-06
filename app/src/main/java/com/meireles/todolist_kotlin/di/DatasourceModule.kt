package com.meireles.todolist_kotlin.di

import com.meireles.todolist_kotlin.data.datasource.TaskLocalDatasource
import com.meireles.todolist_kotlin.data.datasource.TaskLocalDatasourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt responsável por ligar a abstração [TaskLocalDatasource]
 * à sua implementação concreta [TaskLocalDatasourceImpl].
 *
 * As dependências são registradas no [SingletonComponent], garantindo
 * que a mesma instância do datasource seja reutilizada durante todo o
 * ciclo de vida da aplicação.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DatasourceModule {
    /**
     * Informa ao Hilt que, quando alguém solicitar [TaskLocalDatasource],
     * deve injetar uma instância de [TaskLocalDatasourceImpl].
     */
    @Binds
    @Singleton
    abstract fun bindTaskLocalDatasource(impl: TaskLocalDatasourceImpl): TaskLocalDatasource
}
