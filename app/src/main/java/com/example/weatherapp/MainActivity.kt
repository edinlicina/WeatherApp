package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold { innerPadding ->
                        CWeatherEntryList(
                            weatherEntries = weatherEntries,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun CWeatherEntry(weatherEntry: WeatherEntry) {
    Text(
        text = weatherEntry.dateTime

    )
    Text(
        text = weatherEntry.icon

    )
    Text(
        text = weatherEntry.temperature.toString()

    )
}

@Composable
private fun CWeatherEntryList(weatherEntries: List<WeatherEntry>, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        items(weatherEntries) { weatherEntry ->
            CWeatherEntry(weatherEntry = weatherEntry)
        }

    }
}