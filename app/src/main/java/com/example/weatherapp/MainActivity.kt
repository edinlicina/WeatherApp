package com.example.weatherapp

import NotificationService
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapp.ui.detail.DetailScreen
import com.example.weatherapp.ui.home.HomeScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val UNIQUE_WORK_NAME = "download_forecast_periodic"

class MainActivity : ComponentActivity() {

    private val requestNotifyPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiModule.init(applicationContext)
        NotificationService.init(this)

        maybeRequestNotificationPermission()
        NotificationService.showNotification(this, 1, "Title", "Text")
        enqueueForecastWorker()

        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController, startDestination = HomeScreenRoute) {
                        composable<HomeScreenRoute> {
                            HomeScreen(navController)
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

    private fun maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotifyPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun enqueueForecastWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<DownloadForecastWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
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

    private const val GEO_BASE_URL = "https://api.openweathermap.org/geo/1.0/"
    private const val FORECAST_BASE_URL = "https://api.openweathermap.org/data/2.5/"

    @Volatile
    private var initialized = false
    private lateinit var apiKey: String
    private val client: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)

        builder.build()
    }

    private val geoRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GEO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private val forecastRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(FORECAST_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val geoLocationService: GeoLocationService by lazy {
        check(initialized) { "ApiModule.init(context) not called" }
        geoRetrofit.create(GeoLocationService::class.java)
    }

    val forecastService: ForecastService by lazy {
        check(initialized) { "ApiModule.init(context) not called" }
        forecastRetrofit.create(ForecastService::class.java)
    }

    fun init(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (!initialized) {
                apiKey = context.applicationContext.getString(R.string.api_key)
                initialized = true
            }
        }
    }
}
