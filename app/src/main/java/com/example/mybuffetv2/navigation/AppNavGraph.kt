package com.example.mybuffetv2.navigation

import android.app.Activity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mybuffetv2.data.UserPreferences
import com.example.mybuffetv2.model.EventoSeleccionadoManager
import com.example.mybuffetv2.model.ProductoSeleccionadoManager
import com.example.mybuffetv2.ui.screens.RecaudacionScreen
import com.example.mybuffetv2.ui.screens.dashboard.DashboardScreen
import com.example.mybuffetv2.ui.screens.eventos.AgregarEventoScreen
import com.example.mybuffetv2.ui.screens.eventos.EventoDetalleScreen
import com.example.mybuffetv2.ui.screens.loginScreen.LoginScreen
import com.example.mybuffetv2.ui.screens.productos.AgregarProductoScreen
import com.example.mybuffetv2.ui.screens.productos.EditarProductoScreen
import com.example.mybuffetv2.ui.screens.productos.ProductosScreen
import com.example.mybuffetv2.ui.screens.splashScreen.SplashScreen
import com.example.mybuffetv2.ui.screens.buffet.BuffetScreen
import com.example.mybuffetv2.viewmodel.RecaudacionViewModel
import kotlinx.coroutines.launch

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val AGREGAR_EVENTO = "agregarEvento"
    const val EVENTO_DETALLE = "eventoDetalle"
    const val PRODUCTOS_SCREEN = "productos_screen"
    const val AGREGAR_PRODUCTO = "agregarProducto"
    const val EDITAR_PRODUCTO = "editarProducto"
    const val BUFFET_SCREEN = "buffet"
    const val RECAUDACIONES ="recaudacion"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    userPreferences: UserPreferences
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                userPreferences = userPreferences,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                userPreferences = userPreferences
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                navController = navController,
                onNuevoClick = {
                    navController.navigate(Routes.AGREGAR_EVENTO)
                },
                onSalirClick = {
                    (context as? Activity)?.finish()
                },
                onLogoutClick = {
                    scope.launch {
                        userPreferences.setLoggedIn(false)
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.DASHBOARD) { inclusive = true }
                        }
                    }
                },
                onPreferenciasClick = {
                    // Navegación a preferencias si querés
                }
            )
        }

        composable(Routes.AGREGAR_EVENTO) {
            AgregarEventoScreen(
                onGuardarClick = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.AGREGAR_EVENTO) { inclusive = true }
                    }
                },
                onCancelarClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PRODUCTOS_SCREEN) {
            ProductosScreen(
                onAgregarProductoClick = { navController.navigate(Routes.AGREGAR_PRODUCTO) },
                onVolverClick = { navController.navigate(Routes.EVENTO_DETALLE) },
                onEditarProducto = { producto ->
                    ProductoSeleccionadoManager.seleccionarProducto(producto) // paso el producto entero, no solo el id
                    navController.navigate(Routes.EDITAR_PRODUCTO)
                }
            )
        }

        composable(Routes.AGREGAR_PRODUCTO) {
            AgregarProductoScreen(
                onVolver = { navController.popBackStack() },
                onProductoAgregado = { navController.popBackStack() }
            )
        }

        composable(Routes.EDITAR_PRODUCTO) {
            val producto = ProductoSeleccionadoManager.productoSeleccionado
            if (producto == null) {
                // Si no hay producto seleccionado, volvemos al listado
                navController.popBackStack()
            } else {
                EditarProductoScreen(
                    productoId = producto.id,
                    onGuardarClick = {
                       navController.navigate(Routes.PRODUCTOS_SCREEN)
                    },
                    onCancelarClick = {
                       navController.navigate(Routes.PRODUCTOS_SCREEN)
                    }
                )
            }
        }

        composable(Routes.EVENTO_DETALLE) {
            val evento = EventoSeleccionadoManager.eventoSeleccionado
            if (evento == null) {
                // Si no hay evento seleccionado, volvemos al dashboard limpiamente
                navController.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.DASHBOARD) { inclusive = false }
                    launchSingleTop = true
                }
            } else {
                EventoDetalleScreen(
                    eventoId = evento.id,
                    onVolver = {
                        EventoSeleccionadoManager.limpiarSeleccion()
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.DASHBOARD) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onEventoActualizado = {
                        // Si querés hacer algo luego de actualizar
                    },
                    onEventoBorrado = {
                        EventoSeleccionadoManager.limpiarSeleccion()
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.DASHBOARD) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onIrAlBuffet = {
                        navController.navigate(Routes.BUFFET_SCREEN)
                    },
                    onVerRecaudacion = {
                        navController.navigate(Routes.RECAUDACIONES)
                    },
                    onProductos = {
                        navController.navigate(Routes.PRODUCTOS_SCREEN)
                    }
                )
            }
        }
        composable(Routes.BUFFET_SCREEN) {
            BuffetScreen(
                onVolver = {navController.navigate(Routes.EVENTO_DETALLE)}
            )
        }
        composable(Routes.RECAUDACIONES) {
            val viewModel: RecaudacionViewModel = viewModel()
            RecaudacionScreen(
                viewModel = viewModel,
                onVolver = {navController.navigate(Routes.EVENTO_DETALLE)}
                )
        }
    }
}
