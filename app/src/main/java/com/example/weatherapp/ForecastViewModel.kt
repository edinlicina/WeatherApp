package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiStateForecast(
    val loading: Boolean = false,
    val data: ForecastResponse? = null,
    val error: String? = null
)

class ForecastViewModel(private val repository: ForecastRepository = ForecastRepository()) :
    ViewModel() {
    private val _state = MutableStateFlow(UiStateForecast(loading = true))
    val state: StateFlow<UiStateForecast> = _state

    fun fetchForecast() {
        _state.value = UiStateForecast(loading = true)
        viewModelScope.launch {
            val result = repository.loadForecast("48.2083537", "16.3725042")
            _state.value = result.fold(
                onSuccess = { UiStateForecast(data = it) },
                onFailure = { UiStateForecast(error = it.message ?: "Unknown error") }
            )
        }
    }
}