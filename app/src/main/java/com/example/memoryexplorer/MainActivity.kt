package com.example.memoryexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.memoryexplorer.data.repositories.LoginRepository
import com.example.memoryexplorer.ui.MemoryExplorerNavGraph
import com.example.memoryexplorer.ui.MemoryExplorerRoute
import com.example.memoryexplorer.ui.composables.AppBar
import com.example.memoryexplorer.ui.theme.MemoryExplorerTheme
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MemoryExplorerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            MemoryExplorerRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: MemoryExplorerRoute.Home
                        }
                    }

                    val loginRepository = get<LoginRepository>() // Ottieni un'istanza di LoginRepository
                    val rememberMe by loginRepository.remember.collectAsState(initial = false) // Ottieni il valore corrente di REMEMBER_ME

                    Scaffold(
                        topBar = { AppBar(navController, currentRoute) }
                    ) { contentPadding ->
                        MemoryExplorerNavGraph(
                            navController,
                            modifier = Modifier.padding(contentPadding)
                        )

                        // controlla se REMEMBER_ME è true
                        LaunchedEffect(key1 = rememberMe) {
                            if (rememberMe) {
                                navController.navigate(MemoryExplorerRoute.Home.route) {
                                    popUpTo(MemoryExplorerRoute.Login.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(MemoryExplorerRoute.Login.route) {
                                    popUpTo(MemoryExplorerRoute.Home.route) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }
}
