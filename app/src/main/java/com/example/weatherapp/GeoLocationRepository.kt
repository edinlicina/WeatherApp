package com.example.weatherapp

class GeoLocationRepository(private val api: GeoLocationService = ApiModule.geoLocationService) {
    suspend fun loadLocation(q: String): Result<List<GeoLocationResponse>> = try {
        val result = api.getLocation(q = q)
        Result.success(result)
    } catch (t: Throwable) {
        Result.failure(t)
    }
}