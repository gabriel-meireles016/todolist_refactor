package com.meireles.todolist_kotlin.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

const val TODO_DATABASE_NAME = "todolist_kt"

// AppDatabase will have all the features of a Database Room
@Database(entities = [TaskEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {
    // Creating a Dao instance
    abstract fun taskDao(): TaskDao
}