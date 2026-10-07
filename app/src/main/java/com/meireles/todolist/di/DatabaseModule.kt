package com.meireles.todolist.di

import android.content.Context
import androidx.room.Room
import com.meireles.todolist.data.database.AppDatabase
import com.meireles.todolist.data.database.TODO_DATABASE_NAME
import com.meireles.todolist.data.database.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt responsável por prover as instâncias relacionadas ao banco
 * de dados Room do aplicativo.
 *
 * As dependências são registradas no [SingletonComponent], garantindo que
 * exista uma única instância de [AppDatabase] durante todo o ciclo de vida
 * da aplicação.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /**
     * Provê a instância única de [AppDatabase] usada pelo app.
     *
     * Optamos por **não** usar migração destrutiva: se a versão do schema
     * for incrementada sem uma migração correspondente, uma exceção será
     * lançada em vez de apagar os dados do usuário silenciosamente.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                TODO_DATABASE_NAME,
            ).fallbackToDestructiveMigration(false)
            .build()

    /**
     * Provê o [TaskDao] a partir do [AppDatabase] injetado.
     */
    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
}
