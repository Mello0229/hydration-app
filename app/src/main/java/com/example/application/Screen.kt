package com.example.application

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Insights : Screen("insightsScreen")
    object Alerts : Screen("alertScreen")
    object Settings : Screen("settingsScreen")
    object Profile : Screen("profileScreen")
    object Login : Screen("login")
}