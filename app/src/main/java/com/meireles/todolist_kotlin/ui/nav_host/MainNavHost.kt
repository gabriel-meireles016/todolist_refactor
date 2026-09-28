package com.meireles.todolist_kotlin.ui.nav_host

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.meireles.todolist_kotlin.navigation.UIRoute
import com.meireles.todolist_kotlin.ui.screens.add.screen.AddRoute
import com.meireles.todolist_kotlin.ui.screens.home.screen.HomeRoute

/**Main navigation graph of the app.*/
@Composable
fun MainNavHost(modifier: Modifier = Modifier) {

    /**
     * Navigation Controller to orchestrate navigation between Compose destinations.
     * */
    val navController = rememberNavController()

    // Define the NavHost, which maps destinations (routes) to Composable.
    NavHost(navController = navController, startDestination = UIRoute.Home) {

        // Route to Home Screen
        composable<UIRoute.Home> {
            HomeRoute(
                goAdd = { navController.navigate(UIRoute.Add) },
                goEdit = {id -> navController.navigate(UIRoute.Edit(id))}
            )
        }

        // Route to Add Screen
        composable<UIRoute.Add> {
            AddRoute(
                onSavedNavigateBack = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        // Route to Edit Screen with typed argument (taskId: Int)
        composable<UIRoute.Edit> { backStackEntry ->
            AddRoute(
                onSavedNavigateBack = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

    }
}