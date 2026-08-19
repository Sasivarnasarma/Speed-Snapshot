package com.example.speedsnapshot.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * Helper class to handle Location Permission requests and checks.
 * This class uses the modern Activity Result API.
 *
 * @param activity The AppCompatActivity where this helper is used.
 */
class LocationPermissionHelper(private val activity: AppCompatActivity) {

    /**
     * Callback that will be invoked when the permission result is received.
     * Boolean parameter is true if at least one location permission (Fine or Coarse) is granted.
     */
    var onPermissionResult: ((Boolean) -> Unit)? = null

    // Register the permissions callback, which handles the user's response to the system permissions dialog.
    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isFineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val isCoarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (isFineLocationGranted || isCoarseLocationGranted) {
                // At least one location permission was granted.
                onPermissionResult?.invoke(true)
            } else {
                // All location permissions were denied.
                onPermissionResult?.invoke(false)
            }
        }

    /**
     * Checks if the app has been granted either ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION.
     * @return true if permission is granted, false otherwise.
     */
    fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    /**
     * Launches the system permission dialog to request location permissions.
     */
    fun requestLocationPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
