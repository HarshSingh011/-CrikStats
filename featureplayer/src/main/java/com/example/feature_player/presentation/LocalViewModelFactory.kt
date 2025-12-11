package com.example.feature_player.presentation

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelProvider

val LocalViewModelFactory = compositionLocalOf<ViewModelProvider.Factory> {
    error("No ViewModelFactory provided")
}

