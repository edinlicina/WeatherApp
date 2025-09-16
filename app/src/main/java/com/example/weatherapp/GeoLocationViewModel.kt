package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.weatherapp.data.local.DatabaseProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val loading: Boolean = false,
    val data: List<GeoLocationResponse> = emptyList(),
    val error: String? = null
)

class GeoLocationViewModel(

    private val repository: GeoLocationRepository

) : ViewModel() {
    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state

    fun fetch(q: String) {
        _state.value = UiState(loading = true)
        viewModelScope.launch {
            val result = repository.loadLocation(q)
            _state.value = result.fold(
                onSuccess = { UiState(data = it) },
                onFailure = { UiState(error = it.message ?: "Unknown error") }
            )
        }
    }
    companion object{
        val Factory : ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = DatabaseProvider.get(application)
                val repository = GeoLocationRepository(settingsDao = database.settingsDao())
                return GeoLocationViewModel(repository = repository) as T
            }
        }

    }
}