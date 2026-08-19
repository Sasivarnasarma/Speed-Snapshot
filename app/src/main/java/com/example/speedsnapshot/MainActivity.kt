package com.example.speedsnapshot

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speedsnapshot.databinding.ActivityMainBinding
import com.example.speedsnapshot.permissions.LocationPermissionHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    
    // Member 2: Location Permission Helper
    private lateinit var locationPermissionHelper: LocationPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.tvStatus.text = getString(R.string.setup_complete)
        // Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
        
        // Member 3 and 4 can use locationPermissionHelper.hasLocationPermission() 
        // before starting their respective location logic.
    }

    /**
     * Member 2: Actions to take when location permission is denied.
     */
    private fun onLocationPermissionDenied() {
        binding.tvStatus.text = getString(R.string.permission_required)
        Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
    }
}
