package com.example.application

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.*
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp

@Composable
fun ApplicationNavigation(
    navController: NavHostController = rememberNavController(),
    userRole: String
) {
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            if (userRole == "coach") {
                NavigationBar(
                    containerColor = Color.White
                ) {
                    val items = listOf("Overview", "Athletes", "Alerts", "Settings")
                    val icons = listOf(
                        Icons.Default.Home,
                        Icons.Default.Person,
                        Icons.Default.Notifications,
                        Icons.Default.Settings
                    )

                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                                try {
                                    println("Navigating to: $item")
                                    when (index) {
                                        0 -> navController.navigate("coachHomeScreen") {
                                            launchSingleTop = true
                                        }
                                        1 -> navController.navigate("athletesScreen") {
                                            launchSingleTop = true
                                        }
                                        2 -> {
                                            println("Attempting to navigate to CoachAlertScreen")
                                            navController.navigate("coachAlertScreen") {
                                                launchSingleTop = true
                                            }
                                        }
                                        3 -> navController.navigate("coachSettingsScreen") {
                                            launchSingleTop = true
                                        }
                                    }
                                } catch (e: Exception) {
                                    println("Navigation error: ${e.message}")
                                    e.printStackTrace()
                                }
                            },
                            icon = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = item
                                    )
                                    Text(
                                        text = item,
                                        fontSize = 10.sp,
                                        color = if (selectedIndex == index) Color(0xFF6A4DFF) else Color.Gray
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (userRole == "coach") "coachHomeScreen" else Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("coachHomeScreen") { CoachHomeScreen(navController) }
            composable("athletesScreen") { AthletesScreen(navController,) }
            composable("coachAlertScreen") {
                println("Navigated to CoachAlertScreen")
                CoachAlertScreen(navController)
            }
            composable("coachSettingsScreen") { CoachSettingsScreen(navController) }
        }
    }
}