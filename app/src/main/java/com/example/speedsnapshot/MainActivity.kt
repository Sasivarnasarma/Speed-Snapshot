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
    
    // Member 2: Location Permission Helper
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    
    // Member 3: Location Manager
    private lateinit var locationManager: LocationManager

    // Flag to track if updates are active (Member 4)
    private var isTracking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize LocationManager (Member 3)
        locationManager = LocationManager(this, this)

        // Member 2: Initialize and handle permissions
        setupPermissions()

        // Set up button listeners (Member 3 & 4)
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
                binding.tvStatus.text = getString(R.string.calculating)
                binding.btnStart.isEnabled = false
                binding.btnStop.isEnabled = true
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
        binding.tvStatus.text = getString(R.string.status_ready)
        binding.tvSpeed.text = getString(R.string.default_speed)
        binding.tvAccuracy.text = getString(R.string.default_accuracy)
        binding.btnStart.isEnabled = true
        binding.btnStop.isEnabled = false
        Log.d("MainActivity", "Stopped tracking")
    }

    /**
     * Member 2: Setup location permission handling.
     */
    private fun setupPermissions() {
        locationPermissionHelper = LocationPermissionHelper(this)

        // Handle the permission result from the dialog
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
            // We don't necessarily want to pop the dialog immediately on launch
            // if we want the user to click Start first. But the requirement says
            // "Initial check: if not granted, request it." in Member 2's code.
            // I'll keep it for now.
            locationPermissionHelper.requestLocationPermission()
        }
    }

    /**
     * Member 2: Actions to take when location permission is granted.
     */
    private fun onLocationPermissionGranted() {
        binding.tvStatus.text = getString(R.string.status_ready)
        binding.btnStart.isEnabled = true
    }

    /**
     * Member 2: Actions to take when location permission is denied.
     */
    private fun onLocationPermissionDenied() {
        binding.tvStatus.text = getString(R.string.permission_required)
        binding.btnStart.isEnabled = true // Allow them to click start to try again
        Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
    }

    /**
     * Member 3: Implementation of LocationUpdateListener
     */
    override fun onLocationUpdate(location: Location) {
        // Member 4: Process the location to update UI
        processLocation(location)
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
     * Handle Android Lifecycle: Stop updates when Activity is paused or stopped (Member 4)
     */
    override fun onPause() {
        super.onPause()
        if (isTracking) {
            // Requirement says stop updates appropriately in onPause or onStop
            // We'll stop updates but keep isTracking flag so we could potentially resume
            // if we wanted, but the requirement just says stop.
            locationManager.stopLocationUpdates()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isTracking) {
            // If we were tracking before pause, resume updates
            locationManager.startLocationUpdates()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.stopLocationUpdates()
    }
}
