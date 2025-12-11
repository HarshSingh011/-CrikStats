package com.example.crikstats.di

import com.example.crikstats.data.remote.CricketApiService
import com.example.crikstats.data.remote.CricketApiServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCricketApiService(): CricketApiService {
        return CricketApiServiceImpl()
    }
}
