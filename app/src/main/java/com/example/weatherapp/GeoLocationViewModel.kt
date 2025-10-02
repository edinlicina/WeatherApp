package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherapp.data.local.DatabaseProvider
import com.example.weatherapp.data.local.SettingsEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GeoLocationViewModel(private val repository: GeoLocationRepository) : ViewModel() {
    val state: StateFlow<SettingsEntity?> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = null
    )

    fun saveSettings(cityName: String, unit: String) {
        viewModelScope.launch {
            repository.saveSettings(cityName = cityName, unit = unit)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = DatabaseProvider.get(application)
                val repository = GeoLocationRepository(settingsDao = database.settingsDao())
                return GeoLocationViewModel(repository = repository) as T
            }
        }
    }
}
