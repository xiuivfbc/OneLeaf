package com.example.todolists.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todolists.ui.anyRepository.RepositoryScreen
import com.example.todolists.ui.home.HomeScreen
import com.example.todolists.ui.item.ItemEditScreen
import com.example.todolists.ui.navigation.Screen.Item
import com.example.todolists.ui.navigation.Screen.Repository

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ToDoListsNavGraph(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home 屏幕
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToRepository = { repoId ->
                    navController.navigate(Repository.createRoute(repoId))
                }
            )
        }

        // Repository 屏幕
        composable(
            route = Screen.Repository.route,
            arguments = listOf(navArgument("repoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val repoId = backStackEntry.arguments?.getString("repoId")!!
            RepositoryScreen(
                repoId = repoId,
                onNavigateToItem = { itemId ->
                    navController.navigate(Item.createRoute(repoId, itemId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Item 屏幕
        composable(
            route = Screen.Item.route,
            arguments = listOf(
                navArgument("repoId") { type = NavType.StringType },
                navArgument("itemId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val repoId = backStackEntry.arguments?.getString("repoId")!!
            val itemId = backStackEntry.arguments?.getLong("itemId")!!
            ItemEditScreen(
                repoId = repoId,
                itemId = itemId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }