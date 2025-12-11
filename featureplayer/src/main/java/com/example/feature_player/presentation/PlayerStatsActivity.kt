package com.example.feature_player.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.example.crikstats.ui.theme.CrikStatsTheme
import com.example.feature_player.data.remote.CricketApiServiceImpl
import com.example.feature_player.data.repository.PlayerRepository
import com.example.feature_player.presentation.navigation.PlayerStatsNavGraph

class PlayerStatsActivity : ComponentActivity() {

    private val viewModelFactory: PlayerStatsViewModelFactory by lazy {
        val apiService = CricketApiServiceImpl()
        val repository = PlayerRepository(apiService)
        PlayerStatsViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrikStatsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        LocalViewModelFactory provides viewModelFactory
                    ) {
                        PlayerStatsNavGraph(onBackPressed = { finish() })
                    }
                }
            }
        }
    }
}

