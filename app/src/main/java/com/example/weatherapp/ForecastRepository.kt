package com.example.weatherapp

import android.util.Log

class ForecastRepository(private val api: ForecastService = ApiModule.forecastService) {
    suspend fun loadForecast(lat: String, lon: String): Result<ForecastResponse> = try {
        val result = api.getForecast(lat = lat, lon = lon)
        Result.success(result)
    }catch (t: Throwable){
        Log.i("ForecastRepository", t.toString())
        Result.failure(t)
    }
}