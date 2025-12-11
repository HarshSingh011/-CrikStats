package com.example.crikstats.data.remote

import com.example.crikstats.data.model.PlayerStats
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CricketApiServiceImpl @Inject constructor() : CricketApiService {

    override suspend fun getPlayerStats(): PlayerStats {
        delay(500)
        return PlayerStats(
            name = "Virat Kohli",
            matches = 253,
            average = 57.8
        )
    }
}
