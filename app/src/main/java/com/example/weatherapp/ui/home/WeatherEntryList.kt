package com.example.weatherapp.ui.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        items(weatherEntries) { weatherEntry ->
            CWeatherEntry(weatherEntry = weatherEntry) { data ->
                navController.navigate(
                    DetailScreenRoute(
                        icon = data.weather[0].icon,
                        condition = "Nope",
                        dateTime = data.dt_txt,
                        temperature = data.main.temp_max.toString(),
                        pressure = data.main.pressure.toString(),
                        humidity = data.main.humidity.toString(),
                        cloudCover = data.clouds.all.toString(),
                        windSpeed = data.wind.speed.toString(),
                        windDirection = data.wind.deg.toString(),
                        rain = "Nope",
                        snow = "Nope",
                    )
                )
            }
        }
    }
}
