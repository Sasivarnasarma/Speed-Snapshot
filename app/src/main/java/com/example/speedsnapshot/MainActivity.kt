package com.example.speedsnapshot

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speedsnapshot.databinding.ActivityMainBinding
import com.example.speedsnapshot.location.LocationManager
import com.example.speedsnapshot.permissions.LocationPermissionHelper
import com.example.speedsnapshot.utils.SpeedUtils

class MainActivity : AppCompatActivity(), LocationManager.LocationUpdateListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var locationManager: LocationManager

    // Flag to track if updates are active
    private var isTracking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize components
        locationManager = LocationManager(this, this)
        setupPermissions()
        setupButtons()
    }

    /**
     * Set up Start and Stop button click listeners.
     */
    private fun setupButtons() {
        binding.btnStart.setOnClickListener {
            startTracking()
        }

        binding.btnStop.setOnClickListener {
            stopTracking()
        }
    }

    /**
     * Starts location tracking if permissions are granted.
     */
    private fun startTracking() {
        if (locationPermissionHelper.hasLocationPermission()) {
            val started = locationManager.startLocationUpdates()
            if (started) {
                isTracking = true
                updateUIState()
                Log.d("MainActivity", "Started tracking")
            } else {
                Toast.makeText(this, "Failed to start location updates", Toast.LENGTH_SHORT).show()
            }
        } else {
            locationPermissionHelper.requestLocationPermission()
        }
    }

    /**
     * Stops location tracking and resets UI.
     */
    private fun stopTracking() {
        locationManager.stopLocationUpdates()
        isTracking = false
        updateUIState()
        binding.tvSpeed.text = getString(R.string.default_speed)
        binding.tvAccuracy.text = getString(R.string.default_accuracy)
        Log.d("MainActivity", "Stopped tracking")
    }

    /**
     * Synchronizes the UI state based on tracking status.
     */
    private fun updateUIState() {
        if (isTracking) {
            binding.tvStatus.text = getString(R.string.status_tracking)
            binding.btnStart.isEnabled = false
            binding.btnStop.isEnabled = true
        } else {
            binding.tvStatus.text = if (locationPermissionHelper.hasLocationPermission()) {
                getString(R.string.status_ready)
            } else {
                getString(R.string.permission_required)
            }
            binding.btnStart.isEnabled = true
            binding.btnStop.isEnabled = false
        }
    }

    /**
     * Setup location permission handling.
     */
    private fun setupPermissions() {
        locationPermissionHelper = LocationPermissionHelper(this)

        locationPermissionHelper.onPermissionResult = { isGranted ->
            if (isGranted) {
                onLocationPermissionGranted()
            } else {
                onLocationPermissionDenied()
            }
        }

        // Initial check: if not granted, request it.
        if (locationPermissionHelper.hasLocationPermission()) {
            onLocationPermissionGranted()
        } else {
            locationPermissionHelper.requestLocationPermission()
        }
    }

    /**
     * Actions to take when location permission is granted.
     */
    private fun onLocationPermissionGranted() {
        updateUIState()
    }

    /**
     * Actions to take when location permission is denied.
     */
    private fun onLocationPermissionDenied() {
        updateUIState()
        Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
    }

    /**
     * Implementation of LocationUpdateListener
     */
    override fun onLocationUpdate(location: Location) {
        processLocation(location)
    }

    /**
     * Processes the received location, updates speed and accuracy.
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

    override fun onResume() {
        super.onResume()
        updateUIState()
        if (isTracking) {
            locationManager.startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isTracking) {
            locationManager.stopLocationUpdates()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.stopLocationUpdates()
    }
}
