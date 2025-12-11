package com.example.feature_player.data.remote

import com.example.feature_player.data.model.PlayerStats
import retrofit2.http.GET

interface CricketApiService {

    @GET("player/virat-kohli")
    suspend fun getPlayerStats(): PlayerStats
}
