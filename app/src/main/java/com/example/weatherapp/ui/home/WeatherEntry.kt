package com.example.weatherapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.WeatherDataResponse

@Composable
fun CWeatherEntry(weatherEntry: WeatherDataResponse, onClick: (id: WeatherDataResponse) -> Unit) {

    Column(
        modifier = Modifier
            .clickable { onClick(weatherEntry) }
            .padding(16.dp)
    ) {
        Text(text = weatherEntry.dt_txt)
        Text(text = weatherEntry.weather[0].icon)
        Text(text = weatherEntry.main.temp_max.toString() + "°C")
    }
}