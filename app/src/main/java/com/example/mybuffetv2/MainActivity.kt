package com.example.mybuffetv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mybuffetv2.navigation.AppNavGraph
import com.example.mybuffetv2.ui.theme.MyBuffetV2Theme
import com.example.mybuffetv2.data.UserPreferences


class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(applicationContext)

        setContent {
            MyBuffetApp(userPreferences)
        }
    }
}

@Composable
fun MyBuffetApp(userPreferences: UserPreferences) {
    val navController = rememberNavController()
    MyBuffetV2Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavGraph(navController = navController, userPreferences = userPreferences)
        }
    }
}
