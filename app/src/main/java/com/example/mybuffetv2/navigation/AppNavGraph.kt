package com.example.mybuffetv2.navigation

import android.app.Activity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mybuffetv2.data.UserPreferences
import com.example.mybuffetv2.model.EventoSeleccionadoManager
import com.example.mybuffetv2.ui.screens.dashboard.DashboardScreen
import com.example.mybuffetv2.ui.screens.eventos.AgregarEventoScreen
import com.example.mybuffetv2.ui.screens.eventos.EventoDetalleScreen
import com.example.mybuffetv2.ui.screens.loginScreen.LoginScreen
import com.example.mybuffetv2.ui.screens.splashScreen.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    userPreferences: UserPreferences
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
                navController = navController,
                onNuevoClick = {
                    navController.navigate("agregarEvento")
                },
                onSalirClick = {
                    (context as? Activity)?.finish()
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
                    // Navegación a preferencias si querés
                }
            )
        }

        composable("agregarEvento") {
            AgregarEventoScreen(
                onGuardarClick = {
                    navController.navigate("dashboard") {
                        popUpTo("agregarEvento") { inclusive = true }
                    }
                },
                onCancelarClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("eventoDetalle") {
            val evento = EventoSeleccionadoManager.eventoSeleccionado
            if (evento != null) {
                EventoDetalleScreen(
                    eventoId = evento.id,
                    onVolver = {
                        EventoSeleccionadoManager.limpiarSeleccion()
                        navController.popBackStack()
                    },
                    onEventoActualizado = {},
                    onEventoBorrado = {navController.navigate("dashboard")},
                    onIrAlBuffet= {},
                    onVerRecaudacion= {},
                    onProductos= {}
                )
            } else {
                Text("No hay evento seleccionado")
            }
        }
    }
}
