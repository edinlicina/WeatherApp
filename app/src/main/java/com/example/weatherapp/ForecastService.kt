package com.example.weatherapp

import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastService {
    @GET("forecast")
    suspend fun getForecast(@Query("lat") lat: String, @Query("lon") lon: String): ForecastResponse

}