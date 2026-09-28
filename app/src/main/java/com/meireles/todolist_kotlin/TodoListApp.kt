package com.meireles.todolist_kotlin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Initializes global components before any Activity or Composable is created.
 *
 * Registers the MainServiceLocator with the application context, allowing dependencies
 * such as the database and repositories to be created and accessed throughout the app.
 *
 * The annotation informs Hilt that is the Application used to generate the global dependency graph.
 * */
@HiltAndroidApp
class TodoListApp : Application()