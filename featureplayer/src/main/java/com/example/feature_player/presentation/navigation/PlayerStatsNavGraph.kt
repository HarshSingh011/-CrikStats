package com.example.feature_player.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feature_player.presentation.PlayerStatsScreen

sealed class PlayerStatsRoutes(val route: String) {
    object PlayerStats : PlayerStatsRoutes("player_stats")
}

@Composable
fun PlayerStatsNavGraph(onBackPressed: () -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PlayerStatsRoutes.PlayerStats.route
    ) {
        composable(PlayerStatsRoutes.PlayerStats.route) {
            PlayerStatsScreen(onBackPressed = onBackPressed)
        }
    }
}

