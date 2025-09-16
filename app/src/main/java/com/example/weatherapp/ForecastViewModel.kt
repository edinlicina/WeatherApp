package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherapp.data.local.DatabaseProvider
import com.example.weatherapp.data.local.SettingsEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiStateForecast(
    val loading: Boolean = false,
    val data: ForecastResponse? = null,
    val error: String? = null
)

class ForecastViewModel(
    private val forecastRepository: ForecastRepository,
    private val geoLocationRepository: GeoLocationRepository,
) :
    ViewModel() {
    private val _state = MutableStateFlow(UiStateForecast(loading = true))

    val settings = geoLocationRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingsEntity(),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<UiStateForecast> =
        settings
            .flatMapLatest { s ->
                flow {
                    emit(UiStateForecast(loading = true))
                    val result = forecastRepository.loadForecast("${s.cityLat}", "${s.cityLon}")
                    emit(
                        result.fold(
                            onSuccess = { UiStateForecast(data = it) },
                            onFailure = { UiStateForecast(error = it.message ?: "Unknown error") }
                        )
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiStateForecast(loading = true)
            )

    fun fetchForecast() {
        _state.value = UiStateForecast(loading = true)
        viewModelScope.launch {
            val result = forecastRepository.loadForecast("48.2083537", "16.3725042")
            _state.value = result.fold(
                onSuccess = { UiStateForecast(data = it) },
                onFailure = { UiStateForecast(error = it.message ?: "Unknown error") }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = DatabaseProvider.get(application)
                val forecastRepository = ForecastRepository()
                val geoLocationRepository =
                    GeoLocationRepository(settingsDao = database.settingsDao())
                return ForecastViewModel(
                    forecastRepository = forecastRepository,
                    geoLocationRepository = geoLocationRepository
                ) as T
            }
        }
    }
}
