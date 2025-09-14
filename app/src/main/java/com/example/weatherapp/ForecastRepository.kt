package com.example.weatherapp

class ForecastRepository(private val api: ForecastService = ApiModule.forecastService) {
    suspend fun loadForecast(lat: String, lon: String): Result<ForecastResponse> = try {
        val result = api.getForecast(lat = lat, lon = lon)
        Result.success(result)
    }catch (t: Throwable){
        Result.failure(t)
    }
}