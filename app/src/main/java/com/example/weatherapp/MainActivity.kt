package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = HomeScreen,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable<HomeScreen> {
                                CWeatherEntryList(
                                    weatherEntries = weatherEntries,
                                    navController = navController
                                )
                            }
                            composable<DetailScreen> {
                                val args = it.toRoute<DetailScreen>()
                                val weatherEntry =
                                    weatherEntries.find { entry -> entry.id == args.id }
                                if (weatherEntry == null) {
                                    Text(
                                        text = "No Weather Data",
                                        modifier = Modifier.padding(innerPadding),
                                    )
                                } else {
                                    Column {
                                        Text(
                                            text = "Icon: ${weatherEntry.icon}",
                                        )
                                        Text(
                                            text = "Condition: ${weatherEntry.condition}",
                                        )
                                        Text(
                                            text = "Date and Time: ${weatherEntry.dateTime}",
                                        )
                                        Text(
                                            text = "Temperature: ${weatherEntry.temperature}",
                                        )
                                        Text(
                                            text = "Pressure: ${weatherEntry.pressure}",
                                        )
                                        Text(
                                            text = "Humidity: ${weatherEntry.humidity}",
                                        )
                                        Text(
                                            text = "Cloud cover: ${weatherEntry.cloudCover}",
                                        )
                                        Text(
                                            text = "Wind speed: ${weatherEntry.windSpeed}",
                                        )
                                        Text(
                                            text = "Wind direction: ${weatherEntry.windDirection}",
                                        )
                                        Text(
                                            text = "Rain (last 3h): ${weatherEntry.rain}",
                                        )
                                        Text(
                                            text = "Snow (last 3h): ${weatherEntry.snow}",
                                        )

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun CWeatherEntry(weatherEntry: WeatherEntry, onClick: (id: Int) -> Unit) {

    Column(
        modifier = Modifier
            .clickable { onClick(weatherEntry.id) }
            .padding(16.dp)
    ) {
        Text(text = weatherEntry.dateTime)
        Text(text = weatherEntry.icon)
        Text(text = weatherEntry.temperature.toString())
    }
}

@Composable
private fun CWeatherEntryList(
    weatherEntries: List<WeatherEntry>,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    LazyColumn(modifier = modifier) {
        items(weatherEntries) { weatherEntry ->
            CWeatherEntry(weatherEntry = weatherEntry) { id ->
                navController.navigate(DetailScreen(id = id))
            }
        }

    }
}

@Serializable
object HomeScreen

@Serializable
data class DetailScreen(val id: Int)