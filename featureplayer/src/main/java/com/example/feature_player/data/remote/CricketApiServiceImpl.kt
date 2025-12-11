package com.example.feature_player.data.remote

import com.example.feature_player.data.model.PlayerStats
import kotlinx.coroutines.delay

class CricketApiServiceImpl : CricketApiService {

    override suspend fun getPlayerStats(): PlayerStats {
        delay(1500)
        return PlayerStats(
            name = "Virat Kohli",
            matches = 253,
            average = 57.8
        )
    }
}
