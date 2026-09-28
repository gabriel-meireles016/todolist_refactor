package com.meireles.todolist_kotlin.di

import android.content.Context
import androidx.room.Room
import com.meireles.todolist_kotlin.data.database.AppDatabase
import com.meireles.todolist_kotlin.data.database.TODO_DATABASE_NAME
import com.meireles.todolist_kotlin.data.database.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module (Hilt) responsible for providing instances related to the
 * application's Room Database.
 * */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /**Provides an AppDatabase instance for the injection graph.*/
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,                            // Necessary context to create/open database
            AppDatabase::class.java,     // Abstract class that represents the database
            TODO_DATABASE_NAME                  // Database file name
        )
            // If there is a version change without migration, it will throw an exception instead of deleting data.
            .fallbackToDestructiveMigration(false)
            // Build database instance
            .build()

    /**Provides Task's DAO for the injection graph.*/
    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
}