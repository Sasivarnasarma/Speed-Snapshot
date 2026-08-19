package com.example.speedsnapshot.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * Manages continuous location updates for the Speed Snapshot app.
 *
 * This class provides a clean interface for starting and stopping location updates
 * using the Fused Location Provider API.
 */
class LocationManager(private val context: Context, private val listener: LocationUpdateListener) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    /**
     * Interface to receive location updates.
     */
    interface LocationUpdateListener {
        fun onLocationUpdate(location: Location)
    }

    /**
     * Starts receiving continuous location updates.
     *
     * Note: Permission check must be handled by the caller or before calling this.
     * This method performs a safety check and returns false if permissions are missing.
     */
    fun startLocationUpdates(): Boolean {
        if (!hasLocationPermission()) {
            Log.e("LocationManager", "Location permission not granted")
            return false
        }

        // Create the location request
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000) // 2 seconds
            .setMinUpdateIntervalMillis(1000) // 1 second fastest interval
            .build()

        // Create the callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    listener.onLocationUpdate(location)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            Log.d("LocationManager", "Started location updates")
            return true
        } catch (unlikely: SecurityException) {
            Log.e("LocationManager", "Lost location permission. $unlikely")
            return false
        }
    }

    /**
     * Stops receiving location updates.
     */
    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
            Log.d("LocationManager", "Stopped location updates")
        }
    }

    /**
     * Helper method to check for location permissions.
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
