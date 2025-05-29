package com.example.mybuffetv2.ui.screens.splashScreen

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mybuffetv2.data.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    userPreferences: UserPreferences
) {
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1500) // Simula tiempo del splash
        val loggedIn = userPreferences.isLoggedInFlow.first()
        if (loggedIn) {
            onNavigateToDashboard()
        } else {
            onNavigateToLogin()
        }
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando...")
            }
        }
    }
}
