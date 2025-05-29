package com.example.mybuffetv2.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mybuffetv2.data.UserPreferences
import com.example.mybuffetv2.ui.screens.dashboard.DashboardScreen
import com.example.mybuffetv2.ui.screens.loginScreen.LoginScreen
import com.example.mybuffetv2.ui.screens.splashScreen.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    userPreferences: UserPreferences
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()  // <-- Acá declarás scope

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                userPreferences = userPreferences,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                userPreferences = userPreferences
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onNuevoClick = {
                    // lógica nuevo evento
                },
                onSalirClick = {
                    (context as? Activity)?.finish()  // cierra la app
                },
                onLogoutClick = {
                    scope.launch {
                        userPreferences.setLoggedIn(false)
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }
                },
                onPreferenciasClick = {
                    // lógica preferencias
                }
            )
        }
    }
}