package com.meireles.todolist.ui.navhost

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meireles.todolist.navigation.UIRoute
import com.meireles.todolist.ui.screens.add.screen.AddRoute
import com.meireles.todolist.ui.screens.home.screen.HomeRoute

/**
 * Grafo de navegação principal do app.
 *
 * Mapeia cada [UIRoute] para o composable correspondente, conectando
 * a navegação entre as telas Home, Add e Edit.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = UIRoute.Home,
    ) {
        composable<UIRoute.Home> {
            HomeRoute(
                goAdd = { navController.navigate(UIRoute.Add) },
                goEdit = { id ->
                    navController.navigate(
                        UIRoute.Edit(
                            id.value.toInt(),
                        ),
                    )
                },
            )
        }

        composable<UIRoute.Add> {
            AddRoute(
                onSavedNavigateBack = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() },
            )
        }

        composable<UIRoute.Edit> {
            AddRoute(
                onSavedNavigateBack = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
