package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weatherapp.ui.detail.DetailScreen
import com.example.weatherapp.ui.home.HomeScreen
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
                    NavHost(
                        navController = navController,
                        startDestination = HomeScreenRoute,
                    ) {
                        composable<HomeScreenRoute> {
                            HomeScreen(navController = navController)
                        }
                        composable<DetailScreenRoute> {
                            val data = it.toRoute<DetailScreenRoute>()
                            DetailScreen(
                                navController = navController,
                                icon = data.icon,
                                condition = data.condition,
                                dateTime = data.dateTime,
                                temperature = data.temperature,
                                pressure = data.pressure,
                                humidity = data.humidity,
                                cloudCover = data.cloudCover,
                                windSpeed = data.windSpeed,
                                windDirection = data.windDirection,
                                rain = data.rain,
                                snow = data.snow,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Serializable
object HomeScreenRoute

@Serializable
data class DetailScreenRoute(
    val icon: String,
    val condition: String,
    val dateTime: String,
    val temperature: String,
    val pressure: String,
    val humidity: String,
    val cloudCover: String,
    val windSpeed: String,
    val windDirection: String,
    val rain: String,
    val snow: String,
)

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
