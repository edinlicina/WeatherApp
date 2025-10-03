import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weatherapp.ui.getWeatherIconRes

object NotificationService {
    private const val CHANNEL_GENERAL = "weather_general"

    fun init(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_GENERAL,
                "Weather updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "General weather notifications" }
        )
    }

    fun canPostNotifications(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(
        context: Context,
        id: Int,
        title: String,
        text: String,
        icon: String,
        pendingIntent: PendingIntent? = null
    ) {
        val iconResource = getWeatherIconRes(icon)
        val builder = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(iconResource)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply { pendingIntent?.let { setContentIntent(it) } }

        post(context, id, builder)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun post(context: Context, id: Int, builder: NotificationCompat.Builder) {
        val nm = NotificationManagerCompat.from(context)
        if (canPostNotifications(context)) {
            nm.notify(id, builder.build())
        }
    }
}
