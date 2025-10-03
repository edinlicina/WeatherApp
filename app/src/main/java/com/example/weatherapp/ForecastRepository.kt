package com.example.weatherapp

import android.util.Log
import com.example.weatherapp.data.local.ForecastDao
import com.example.weatherapp.data.local.ForecastEntity
import com.example.weatherapp.data.local.SettingsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

data class ForecastKey(
    val cityName: String,
    val lat: Float,
    val lon: Float,
    val unit: String
)

data class GetForecastReturn(
    val all: List<ForecastEntity>,
    val changed: List<ForecastEntity>
)

class ForecastRepository(
    private val api: ForecastService = ApiModule.forecastService,
    private val forecastDao: ForecastDao,
    private val settingsDao: SettingsDao
) {
    private val refreshRequests = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)

    suspend fun clear() {
        forecastDao.clear()
    }

    fun refresh(force: Boolean = true) {
        refreshRequests.tryEmit(force)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getForecast(): Flow<GetForecastReturn> {
        return settingsDao.getLatestSettings()
            .filterNotNull()
            .map { ForecastKey(it.cityName, it.cityLat, it.cityLon, it.unit) }
            .distinctUntilChanged()
            .flatMapLatest { key ->
                refreshRequests
                    .onStart { emit(false) }
                    .conflate()
                    .flatMapLatest { force ->
                        flow {
                            val localFlow = forecastDao.getByCityName(key.cityName, key.unit)
                            val local = localFlow.first()

                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val oldDate = local.firstOrNull()?.dateTime?.let { ts ->
                                runCatching { sdf.parse(ts) }.getOrNull()
                            }
                            val isStale = oldDate?.let {
                                System.currentTimeMillis() - it.time >= TimeUnit.MINUTES.toMillis(55)
                            } != false

                            val beforeFetch = local

                            if (force || local.isEmpty() || isStale) {
                                fetchForecast(lat = key.lat, lon = key.lon, cityName = key.cityName)
                            }

                            val afterFetch = localFlow.first()
                            val changed = diffForecasts(beforeFetch, afterFetch)

                            emit(GetForecastReturn(all = afterFetch, changed = changed))

                            emitAll(
                                localFlow
                                    .drop(1)
                                    .distinctUntilChanged()
                                    .map { all ->
                                        GetForecastReturn(all = all, changed = emptyList())
                                    }
                            )
                        }
                    }
            }
            .flowOn(Dispatchers.IO)
    }

    private fun diffForecasts(
        prev: List<ForecastEntity>,
        curr: List<ForecastEntity>
    ): List<ForecastEntity> {
        fun ForecastEntity.key(): String = "$cityName|$unit|$dateTime"
        val prevMap = prev.associateBy { it.key() }
        return curr.filter { e ->
            val prevEntity = prevMap[e.key()]
            prevEntity == null ||
                    prevEntity.temperature != e.temperature ||
                    prevEntity.windSpeed != e.windSpeed ||
                    prevEntity.humidity != e.humidity ||
                    prevEntity.pressure != e.pressure ||
                    prevEntity.cloudCover != e.cloudCover ||
                    prevEntity.windDirection != e.windDirection ||
                    prevEntity.iconCode != e.iconCode ||
                    prevEntity.description != e.description
        }
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
            Log.e("ForecastRepository", t.toString())
            return false
        }
    }

    private fun responseToEntities(
        response: ForecastResponse,
        cityName: String,
        unit: String,
    ): List<ForecastEntity> {
        return response.list.map {
            val w = it.weather.firstOrNull()
            ForecastEntity(
                cityName = cityName,
                windDirection = it.wind.deg,
                temperature = it.main.temp,
                cloudCover = it.clouds.all,
                humidity = it.main.humidity,
                pressure = it.main.pressure,
                dateTime = it.dt_txt,
                windSpeed = it.wind.speed,
                iconCode = w?.icon ?: "",
                unit = unit,
                description = w?.description ?: ""
            )
        }
    }
}
