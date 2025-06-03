package com.example.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()

    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("createProfile/{role}") { backStackEntry ->
            CreateProfileScreenUI(navController, backStackEntry.arguments?.getString("role") ?: "")
        }
        composable("profileSetupScreen") { ProfileSetupScreen(navController) }
        composable("profileSetupScreen1") { ProfileSetupScreen1(navController) }
        composable("home") { HomeScreen(navController, sharedViewModel) }
        composable("insightsScreen") { InsightsScreen(navController, sharedViewModel) }
        composable("settingsScreen") { SettingsScreen(navController) }
        composable("alertScreen") { AlertScreen(navController, sharedViewModel) }
//        composable("profileScreen") { ProfileScreen(navController) }
//        composable("accountScreen") { AccountScreen(navController) }
//        composable("changePasswordScreen") { ChangePasswordScreen(navController) }
//        composable("unitsScreen") { UnitsScreen(navController) }
//        composable("deleteAccountScreen") { DeleteAccountScreen(navController) }
//        composable("notificationsScreen") { NotificationsScreen(navController) }
//        composable("helpScreen") { HelpScreen(navController) }
        composable("coachHomeScreen") { CoachHomeScreen(navController) }
        composable("athletesScreen") { AthletesScreen(navController) }
        composable("coachAlertScreen") { CoachAlertScreen(navController) }
        composable("coachSettingsScreen") { CoachSettingsScreen(navController) }
//        composable("coachProfileScreen") { CoachProfileScreen(navController) }
//        composable("coachAccountScreen") { CoachAccountScreen(navController) }
//        composable("coachNotificationsScreen") { CoachNotificationsScreen(navController) }
//        composable("coachChangePasswordScreen") { CoachChangePasswordScreen(navController) }
//        composable("coachUnitsScreen") { CoachUnitsScreen(navController) }
//        composable("coachDeleteAccountScreen") { CoachDeleteAccountScreen(navController) }
//        composable("coachHelpScreen") { CoachHelpScreen(navController) }
//        composable("teamManagementScreen") { TeamManagementScreen(navController) }
    }
}