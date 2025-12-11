package com.example.feature_player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.feature_player.data.model.PlayerStats
import com.example.feature_player.data.repository.PlayerRepository
import com.example.feature_player.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerStatsViewModel(
    private val repository: PlayerRepository
) : ViewModel() {

    private val _playerStats = MutableStateFlow<PlayerStats?>(null)
    val playerStats: StateFlow<PlayerStats?> = _playerStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadPlayerStats()
    }

    fun loadPlayerStats() {
        viewModelScope.launch {
            repository.getPlayerStats().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                        _error.value = null
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _playerStats.value = resource.data
                        _error.value = null
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _error.value = resource.message
                    }
                }
            }
        }
    }
}

class PlayerStatsViewModelFactory(
    private val repository: PlayerRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerStatsViewModel::class.java)) {
            return PlayerStatsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

