package info.cafferata.rnhtracker.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import info.cafferata.rnhtracker.R

/**
 * Foreground service that keeps requesting GPS fixes while the app is open, including in the
 * background — matches the iOS app's `allowsBackgroundLocationUpdates`. Always live-updates
 * [TrackingRepository]'s current position; whether fixes are also recorded into the GPX
 * session is controlled by [TrackingRepository.start]/[TrackingRepository.stop].
 *
 * Uses the plain platform `LocationManager` rather than the Play Services fused location
 * client — no Google Play Services dependency needed, and it works on every Android device
 * (fused location silently fails on Play-Services-less devices, e.g. this project's own test
 * emulator, which deliberately uses the non-Google system image — see the Riskonacci Android
 * port for the same choice).
 */
class TrackingService : Service() {

    private val locationManager by lazy { getSystemService(LOCATION_SERVICE) as LocationManager }

    private val listener = LocationListener { location: Location -> TrackingRepository.onLocation(location) }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        locationManager.removeUpdates(listener)
        super.onDestroy()
    }

    private fun startLocationUpdates() {
        // desiredAccuracy = best, distanceFilter = 2m — same defaults as the iOS app.
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 2f, listener)
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 2f, listener)
            }
        } catch (e: SecurityException) {
            stopSelf()
        }
    }

    private fun buildNotification(): Notification {
        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Locatie volgen", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RNH Tracker")
            .setContentText("Locatie wordt gevolgd")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "tracking"
        private const val NOTIFICATION_ID = 1
    }
}
