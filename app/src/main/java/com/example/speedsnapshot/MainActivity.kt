package com.example.speedsnapshot

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.speedsnapshot.databinding.ActivityMainBinding
import com.example.speedsnapshot.location.LocationManager

class MainActivity : AppCompatActivity(), LocationManager.LocationUpdateListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize LocationManager
        locationManager = LocationManager(this, this)

        // Placeholder for Start/Stop integration (to be handled by other members)
        // For demonstration, we could call startLocationUpdates if permissions are granted.
        // But for now, we leave it for the integration member.
    }

    override fun onLocationUpdate(location: Location) {
        // This is where location updates are received.
        // Member 4 will use this to update speed and accuracy on the UI.
        Log.d("MainActivity", "New location: ${location.latitude}, ${location.longitude}")
        Log.d("MainActivity", "Speed: ${location.speed} m/s, Accuracy: ${location.accuracy} m")
    }

    override fun onPause() {
        super.onPause()
        // Ensure updates are stopped when the activity is not visible
        locationManager.stopLocationUpdates()
    }
}
