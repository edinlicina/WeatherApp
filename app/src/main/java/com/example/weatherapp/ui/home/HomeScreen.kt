package com.example.weatherapp.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapp.ForecastViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    vm: ForecastViewModel = viewModel(factory = ForecastViewModel.Factory),
) {
    val state by vm.state.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }
    var isDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Weather App")
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = {
                                isExpanded = !isExpanded
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            onDismissRequest = { isExpanded = false },
                            expanded = isExpanded
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    isExpanded = false
                                    isDialogOpen = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Reload") },
                                onClick = {
                                    vm.fetchForecast()
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.loading -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(innerPadding)
                )
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
        if (isDialogOpen == true) {
            SettingsDialog(onDismissRequest = { isDialogOpen = false })
        }
    }
}