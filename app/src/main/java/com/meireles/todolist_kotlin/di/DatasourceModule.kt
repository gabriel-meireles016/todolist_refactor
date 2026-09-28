package com.meireles.todolist_kotlin.di

import com.meireles.todolist_kotlin.data.datasource.TaskLocalDatasource
import com.meireles.todolist_kotlin.data.datasource.TaskLocalDatasourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module (Hilt) for the local datasource layer.
 * */
@Module
@InstallIn(SingletonComponent::class)
abstract class DatasourceModule {

    /**
     * Informs Hilt that when someone needs TaskLocalDatasource, should
     * inject the TaskLocalDatasourceImpl.
     * */
    @Binds @Singleton
    abstract fun bindTaskLocalDatasource(
        impl: TaskLocalDatasourceImpl
    ): TaskLocalDatasource
}