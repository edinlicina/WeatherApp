package com.example.weatherapp

import com.example.weatherapp.data.local.SettingsDao
import com.example.weatherapp.data.local.SettingsEntity
import kotlinx.coroutines.flow.map

class GeoLocationRepository(
    private val api: GeoLocationService = ApiModule.geoLocationService,
    private val settingsDao: SettingsDao
) {
    val settings = settingsDao.getLatestSettings().map { it ?: SettingsEntity() }

    suspend fun loadLocation(q: String): Result<List<GeoLocationResponse>> {
        val result = runCatching {
            api.getLocation(q = q)
        }
        result.onSuccess { response ->
            if (response.isEmpty()) {
                return@onSuccess
            }
            val bestGuess = response[0]
            settingsDao.insertSettings(
                SettingsEntity(
                    id = 0,
                    cityName = bestGuess.name,
                    cityLat = bestGuess.lat,
                    cityLon = bestGuess.lon
                )
            )
        }
        return result
    }
}
