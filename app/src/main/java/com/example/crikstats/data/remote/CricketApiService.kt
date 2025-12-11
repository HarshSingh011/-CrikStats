package com.example.crikstats.data.remote

import com.example.crikstats.data.model.PlayerStats
import retrofit2.http.GET

interface CricketApiService {

    @GET("player/virat-kohli")
    suspend fun getPlayerStats(): PlayerStats
}
