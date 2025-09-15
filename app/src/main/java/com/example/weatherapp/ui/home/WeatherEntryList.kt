package com.example.weatherapp.ui.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.DetailScreenRoute
import com.example.weatherapp.WeatherDataResponse

@Composable
fun CWeatherEntryList(
    weatherEntries: List<WeatherDataResponse>,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(weatherEntries) { index, weatherEntry ->
            val onClick = {
                navController.navigate(
                    DetailScreenRoute(
                        icon = weatherEntry.weather[0].icon,
                        condition = weatherEntry.weather[0].description,
                        dateTime = weatherEntry.dt_txt,
                        temperature = weatherEntry.main.temp_max.toString(),
                        pressure = weatherEntry.main.pressure.toString(),
                        humidity = weatherEntry.main.humidity.toString(),
                        cloudCover = weatherEntry.clouds.all.toString(),
                        windSpeed = weatherEntry.wind.speed.toString(),
                        windDirection = weatherEntry.wind.deg.toString(),
                        rain = "Nope",
                        snow = "Nope",
                    )
                )
            }

            if (index == 0) {
                FirstForecastCard(item = weatherEntry, onClick = onClick)
            } else {
                ForecastRow(item = weatherEntry, onClick = onClick)
                if (index != weatherEntries.lastIndex) Divider()
            }
        }

        // Kleiner Abstand am Ende
        item { Spacer(Modifier.height(12.dp)) }
    }
}