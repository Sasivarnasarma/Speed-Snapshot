package com.example.speedsnapshot

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speedsnapshot.databinding.ActivityMainBinding
import com.example.speedsnapshot.location.LocationManager
import com.example.speedsnapshot.permissions.LocationPermissionHelper

class MainActivity : AppCompatActivity(), LocationManager.LocationUpdateListener {

    private lateinit var binding: ActivityMainBinding
    
    // Member 2: Location Permission Helper
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    
    // Member 3: Location Manager
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize LocationManager (Member 3)
        locationManager = LocationManager(this, this)

        // Member 2: Initialize and handle permissions
        setupPermissions()
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
            locationPermissionHelper.requestLocationPermission()
        }
    }

    /**
     * Member 2: Actions to take when location permission is granted.
     */
    private fun onLocationPermissionGranted() {
        binding.tvStatus.text = getString(R.string.status_ready)
    }

    /**
     * Member 2: Actions to take when location permission is denied.
     */
    private fun onLocationPermissionDenied() {
        binding.tvStatus.text = getString(R.string.permission_required)
        Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
    }

    override fun onLocationUpdate(location: Location) {
        // This is where location updates are received.
        // Member 4 will use this to update speed and accuracy on the UI.
        Log.d("MainActivity", "New location: ${location.latitude}, ${location.longitude}")
        Log.d("MainActivity", "Speed: ${location.speed} m/s, Accuracy: ${location.accuracy} m")
    }

    override fun onPause() {
        super.onPause()
        // Ensure updates are stopped when the activity is not visible (Member 3)
        locationManager.stopLocationUpdates()
    }
}
