package com.example.crikstats.data.repository

import com.example.crikstats.data.model.PlayerStats
import com.example.crikstats.data.remote.CricketApiService
import com.example.crikstats.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
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
