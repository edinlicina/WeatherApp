package com.example.weatherapp

import com.example.weatherapp.data.local.SettingsDao
import com.example.weatherapp.data.local.SettingsEntity

class GeoLocationRepository(
    private val api: GeoLocationService = ApiModule.geoLocationService,
    private val settingsDao: SettingsDao
) {
    suspend fun loadLocation(q: String): Result<List<GeoLocationResponse>> {
        val result = runCatching {
            api.getLocation(q = q)
        }
        result.onSuccess { response ->
            val bestGuess = response[0]
            settingsDao.insertSettings(SettingsEntity(cityName = bestGuess.name, cityLat = bestGuess.lat, cityLon = bestGuess.lon))
        }
        return result
    }

}