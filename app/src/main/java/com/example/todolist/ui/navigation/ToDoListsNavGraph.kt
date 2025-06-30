package com.example.todolist.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todolist.ui.anyRepository.RepositoryScreen
import com.example.todolist.ui.home.HomeScreen
import com.example.todolist.ui.item.ItemEditScreen
import com.example.todolist.ui.navigation.Screen.Item
import com.example.todolist.ui.navigation.Screen.Repository

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ToDoListsNavGraph(
    navController: NavHostController,
    modifier: Modifier
) {
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            // Home 屏幕
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToRepository = { repoId ->
                        navController.navigateSingleTopTo(Repository.createRoute(repoId))
                    }
                )
            }

            // Repository 屏幕
            composable(
                route = Repository.route,
                arguments = listOf(navArgument("repoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val repoId = backStackEntry.arguments?.getString("repoId")!!
                RepositoryScreen(
                    repoId = repoId,
                    onNavigateToItem = { itemId ->
                        navController.navigateSingleTopTo(Item.createRoute(repoId, itemId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Item 屏幕
            composable(
                route = Item.route,
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
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        launchSingleTop = true
        restoreState = true
    }