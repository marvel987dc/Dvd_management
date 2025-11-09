package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movieapp.ui.screens.AddMovieScreen
import com.example.movieapp.ui.screens.EditMovieScreen
import com.example.movieapp.ui.screens.HomeScreen
import com.example.movieapp.ui.theme.MovieAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieAppTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                val navController = rememberNavController()

                Surface(color = MaterialTheme.colorScheme.background) {
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(navController = navController)
                        }
                        composable("addMovie") {
                            AddMovieScreen(navController = navController)
                        }
                        composable(
                            route = "editMovie/{movieId}",
                            arguments = listOf(
                                navArgument("movieId") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                            EditMovieScreen(navController = navController, movieId = movieId)
                        }
                    }
                }
            }
        }
    }
}
