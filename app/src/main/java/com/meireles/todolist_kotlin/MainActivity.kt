package com.meireles.todolist_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.meireles.todolist_kotlin.ui.nav_host.MainNavHost
import com.meireles.todolist_kotlin.ui.screens.home.screen.HomeScreen
import com.meireles.todolist_kotlin.ui.theme.Todolist_kotlinTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity of the app.
 *
 * Annotated with @AndroidEntryPoint to allow injection via Hilt in itself and in composes
 * that depend on the scope of the Activity.
 * */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Todolist_kotlinTheme {
                MainNavHost()
            }
        }
    }
}
