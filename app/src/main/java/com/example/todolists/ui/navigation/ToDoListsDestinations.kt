package com.example.todolists.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    data object Repository : Screen("repository/{repoId}") {
        fun createRoute(repoId: String) = "repository/$repoId"
    }

    data object Item : Screen("repository/{repoId}/item/{itemId}") {
        fun createRoute(repoId: String, itemId: Long) = "repository/$repoId/item/$itemId"
    }
}
