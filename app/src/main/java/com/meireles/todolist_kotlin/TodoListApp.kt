package com.meireles.todolist_kotlin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe `Application` do app.
 *
 * Anotada com `@HiltAndroidApp` para que o Hilt gere o grafo global de
 * dependências e permita injeção em Activities, ViewModels e outros
 * componentes do app.
 */
@HiltAndroidApp
class TodoListApp : Application()