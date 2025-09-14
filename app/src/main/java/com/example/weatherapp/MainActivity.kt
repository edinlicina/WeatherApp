package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiModule.init(this)
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
                                HomeScreen(navController = navController)
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
private fun HomeScreen(navController: NavController, vm: ForecastViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.fetchForecast() }

    when {
        state.loading -> Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }

        else -> {
            Log.i("Habibi", state.data.toString());
            val data = state.data
            if (data != null) {
                CWeatherEntryList(
                    weatherEntries = data.list,
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun CWeatherEntry(weatherEntry: WeatherDataResponse, onClick: (id: Int) -> Unit) {

    Column(
        modifier = Modifier
            .clickable { onClick(weatherEntry.dt) }
            .padding(16.dp)
    ) {
        Text(text = weatherEntry.dt_txt)
        Text(text = weatherEntry.weather[0].icon)
        Text(text = weatherEntry.main.temp_max.toString())
    }
}

@Composable
private fun CWeatherEntryList(
    weatherEntries: List<WeatherDataResponse>,
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

object ApiModule {
    @Volatile
    private var _service: GeoLocationService? = null

    @Volatile
    private var _forecastService: ForecastService? = null

    fun init(context: Context) {
        val apiKey = context.getString(R.string.api_key)
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/geo/1.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        _service = retrofit.create(GeoLocationService::class.java)
        val forecastClient = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        _forecastService = forecastClient.create(ForecastService::class.java)
    }

    val geoLocationService: GeoLocationService
        get() = checkNotNull(_service) { "ApiModule.init(context) not called" }
    val forecastService: ForecastService
        get() = checkNotNull(_forecastService) { "ApiModule.init(context) not called" }

}