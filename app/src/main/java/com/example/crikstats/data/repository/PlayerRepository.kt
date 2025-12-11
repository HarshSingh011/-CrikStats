package com.example.crikstats.data.repository

import com.example.crikstats.data.model.PlayerStats
import com.example.crikstats.data.remote.CricketApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val apiService: CricketApiService
) {
    suspend fun getPlayerStats(): Result<PlayerStats> {
        return try {
            val stats = apiService.getPlayerStats()
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
