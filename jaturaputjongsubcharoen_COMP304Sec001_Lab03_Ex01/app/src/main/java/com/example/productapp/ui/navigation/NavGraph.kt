package com.example.productapp.ui.navigation
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.productapp.ui.screens.AddProductScreen
import com.example.productapp.ui.screens.EditProductScreen
import com.example.productapp.ui.screens.FavoritesScreen
import com.example.productapp.ui.screens.HomeScreen

// Sets up navigation routes for the app
@Composable
fun AppNavigation(navController: NavHostController, windowSizeClass: WindowSizeClass) {
    val isExpanded = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    // Start on home_detail if expanded, otherwise home
    val startDestination = if (isExpanded) "home_detail" else "home"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") {
            HomeScreen(navController, windowSizeClass = windowSizeClass)
        }
        composable("home_detail") {
            HomeScreen(navController, windowSizeClass = windowSizeClass)
        }
        composable("add") {
            AddProductScreen(navController)
        }
        composable(
            route = "edit/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            EditProductScreen(
                productId = backStackEntry.arguments?.getInt("productId"),
                navController = navController
            )
        }
        composable("favorites") {
            FavoritesScreen(navController, windowSizeClass = windowSizeClass)
        }
    }
}
