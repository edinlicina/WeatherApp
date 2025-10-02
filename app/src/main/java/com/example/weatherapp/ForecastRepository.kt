package com.example.weatherapp

import android.util.Log
import com.example.weatherapp.data.local.ForecastDao
import com.example.weatherapp.data.local.ForecastEntity
import com.example.weatherapp.data.local.SettingsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data class ForecastKey(
    val cityName: String,
    val lat: Float,
    val lon: Float,
    val unit: String
)

class ForecastRepository(
    private val api: ForecastService = ApiModule.forecastService,
    private val forecastDao: ForecastDao,
    private val settingsDao: SettingsDao
) {
    private val refreshRequests = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)

    fun refresh(force: Boolean = true) {
        refreshRequests.tryEmit(force)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getForecast(): Flow<List<ForecastEntity>> {
        return settingsDao.getLatestSettings()
            .filterNotNull()
            .map { ForecastKey(it.cityName, it.cityLat, it.cityLon, it.unit) }
            .distinctUntilChanged()
            .flatMapLatest { key ->
                merge(
                    flowOf(false),
                    refreshRequests
                ).flatMapLatest { force ->
                    flow {
                        val local = forecastDao.getByCityName(key.cityName, key.unit).first()
                        if (force || local.isEmpty()) {
                            fetchForecast(lat = key.lat, lon = key.lon, cityName = key.cityName)
                        }
                        emitAll(forecastDao.getByCityName(key.cityName, key.unit))
                    }
                }
            }
            .flowOn(Dispatchers.IO)
    }

    private suspend fun fetchForecast(lat: Float, lon: Float, cityName: String): Boolean {
        forecastDao.clear()

        try {
            val metricResult = api.getForecast(lat = "$lat", lon = "$lon")
            val imperialResult = api.getForecast(lat = "$lat", lon = "$lon", units = "imperial")
            val standardResult = api.getForecast(lat = "$lat", lon = "$lon", units = "standard")

            val metricEntities = responseToEntities(metricResult, cityName, "metric")
            val imperialEntities = responseToEntities(imperialResult, cityName, "imperial")
            val standardEntities = responseToEntities(standardResult, cityName, "standard")

            forecastDao.insertAll(metricEntities)
            forecastDao.insertAll(imperialEntities)
            forecastDao.insertAll(standardEntities)

            return true
        } catch (t: Throwable) {
            Log.i("ForecastRepository", t.toString())
            return false
        }
    }

    private fun responseToEntities(
        response: ForecastResponse,
        cityName: String,
        unit: String,
    ): List<ForecastEntity> {
        return response.list.map {
            ForecastEntity(
                cityName = cityName,
                windDirection = it.wind.deg,
                temperature = it.main.temp,
                cloudCover = it.clouds.all,
                humidity = it.main.humidity,
                pressure = it.main.pressure,
                dateTime = it.dt_txt,
                windSpeed = it.wind.speed,
                iconCode = it.weather[0].icon,
                unit = unit,
                description = it.weather[0].description
            )
        }
    }
}
