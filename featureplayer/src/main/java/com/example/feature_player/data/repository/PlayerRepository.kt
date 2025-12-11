package com.example.feature_player.data.repository

import com.example.feature_player.data.model.PlayerStats
import com.example.feature_player.data.remote.CricketApiService
import com.example.feature_player.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlayerRepository(
    private val apiService: CricketApiService
) {

    fun getPlayerStats(): Flow<Resource<PlayerStats>> = flow {
        try {
            emit(Resource.Loading())
            val stats = apiService.getPlayerStats()
            emit(Resource.Success(stats))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = e.localizedMessage ?: "An unexpected error occurred"
            ))
        }
    }
}
