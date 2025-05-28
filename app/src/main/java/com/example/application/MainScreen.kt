package com.example.application

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController

@Composable
fun MainScreen() {
    val navController: NavHostController = rememberNavController()
    Scaffold(
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = navController, startDestination = "homeScreen") {
                composable("homeScreen") {
                    val sharedViewModel: SharedViewModel = viewModel()
                    HomeScreen(navController = navController, sharedViewModel = sharedViewModel)
                }
            }
        }
    }
}