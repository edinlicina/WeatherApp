package com.example.weatherapp.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapp.ForecastViewModel

@Composable
fun HomeScreen(navController: NavController, vm: ForecastViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.fetchForecast() }
    Scaffold { innerPadding ->
        when {
            state.loading -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }

            else -> {
                val data = state.data
                if (data != null) {
                    CWeatherEntryList(
                        weatherEntries = data.list,
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}