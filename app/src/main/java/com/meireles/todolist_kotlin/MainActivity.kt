package com.meireles.todolist_kotlin

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.meireles.todolist_kotlin.ui.nav_host.MainNavHost
import com.meireles.todolist_kotlin.ui.theme.TodolistKotlinTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Atividade principal do app.
 *
 * Anotada com `@AndroidEntryPoint` para permitir injeção via Hilt na própria
 * Activity e nos composables que dependem do seu escopo.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodolistKotlinTheme {
                MainNavHost()
            }
        }
    }
}