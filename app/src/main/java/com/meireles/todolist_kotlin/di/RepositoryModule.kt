package com.meireles.todolist_kotlin.di

import com.meireles.todolist_kotlin.data.repositories.TaskRepositoryImpl
import com.meireles.todolist_kotlin.domain.repositories.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module (Hilt) for the repository layer.
 * */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    /**
     * Informs Hilt that when someone needs TaskRepository, should
     * inject the TaskRepositoryImpl.
     * */
    @Binds @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository
}