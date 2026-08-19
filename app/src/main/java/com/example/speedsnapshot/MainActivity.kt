package com.example.speedsnapshot

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.speedsnapshot.databinding.ActivityMainBinding
import com.example.speedsnapshot.utils.SpeedUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    // Member 3 will use this for location updates
    private lateinit var locationCallback: LocationCallback
    
    // Flag to track if updates are active, useful for lifecycle cleanup
    private var isTracking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initializing the location callback
        // Member 4: Added location processing logic here
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    processLocation(location)
                }
            }
        }

        // Member 3: Will implement Start Button logic to begin location updates
        binding.btnStart.setOnClickListener {
            // Placeholder: Member 3 should call startLocationUpdates() here
            // After starting, set isTracking = true
            isTracking = true
            binding.tvSpeed.text = getString(R.string.calculating)
        }

        // Member 4: Implement Stop Button logic
        binding.btnStop.setOnClickListener {
            stopLocationUpdates()
        }
    }

    /**
     * Processes the received location, updates speed and accuracy.
     * Task assigned to Member 4.
     */
    private fun processLocation(location: Location) {
        // 1. Read speed and convert from m/s to km/h
        if (location.hasSpeed()) {
            val speedMs = location.speed
            val speedKmh = SpeedUtils.convertMsToKmh(speedMs)
            
            // 2. Update speed UI
            binding.tvSpeed.text = SpeedUtils.formatSpeed(speedKmh)
        } else {
            // Handle cases where speed is not available
            binding.tvSpeed.text = getString(R.string.default_speed)
        }

        // 3. Update accuracy UI
        binding.tvAccuracy.text = SpeedUtils.formatAccuracy(location.accuracy)
    }

    /**
     * Stops continuous location updates and cleans up UI.
     * Task assigned to Member 4.
     */
    private fun stopLocationUpdates() {
        if (!isTracking) return

        fusedLocationClient.removeLocationUpdates(locationCallback)
            .addOnCompleteListener {
                isTracking = false
                binding.tvSpeed.text = getString(R.string.default_speed)
                binding.tvAccuracy.text = getString(R.string.default_accuracy)
                Log.d("MainActivity", "Location updates stopped by user or lifecycle")
            }
    }

    /**
     * Handle Android Lifecycle: Stop updates when Activity is paused or stopped
     * Task assigned to Member 4.
     */
    override fun onPause() {
        super.onPause()
        if (isTracking) {
            stopLocationUpdates()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Final safety cleanup
        if (isTracking) {
            stopLocationUpdates()
        }
    }
}
