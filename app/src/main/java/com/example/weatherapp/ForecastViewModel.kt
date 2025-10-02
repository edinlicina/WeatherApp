package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherapp.data.local.DatabaseProvider
import com.example.weatherapp.data.local.ForecastEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

data class UiStateForecast(
    val loading: Boolean = false,
    val data: List<ForecastEntity>? = null,
    val error: String? = null
)

class ForecastViewModel(
    private val forecastRepository: ForecastRepository,
) : ViewModel() {
    val forecastState: StateFlow<UiStateForecast> =
        forecastRepository.getForecast()
            .map<List<ForecastEntity>, UiStateForecast> { entities ->
                UiStateForecast(
                    loading = false,
                    data = entities,
                    error = null
                )
            }
            .onStart {
                emit(UiStateForecast(loading = true))
            }
            .catch { e ->
                emit(
                    UiStateForecast(
                        loading = false,
                        data = null,
                        error = e.message ?: "Unknown error"
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiStateForecast(loading = true)
            )

    fun refresh() {
        forecastRepository.refresh(force = true)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = DatabaseProvider.get(application)
                val forecastRepository = ForecastRepository(
                    settingsDao = database.settingsDao(),
                    forecastDao = database.forecastDao(),
                )
                return ForecastViewModel(
                    forecastRepository = forecastRepository,
                ) as T
            }
        }
    }
}
