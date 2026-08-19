package com.example.speedsnapshot.utils

import java.util.Locale

object SpeedUtils {

    /**
     * Converts speed from meters per second to kilometers per hour.
     * Formula: km/h = m/s * 3.6
     */
    fun convertMsToKmh(speedMs: Float): Float {
        return speedMs * 3.6f
    }

    /**
     * Formats speed for display.
     * Example: "18.5 km/h"
     */
    fun formatSpeed(speedKmh: Float): String {
        return String.format(Locale.getDefault(), "%.1f km/h", speedKmh)
    }

    /**
     * Formats accuracy for display.
     * Example: "±5.2 m"
     */
    fun formatAccuracy(accuracyMeters: Float): String {
        return String.format(Locale.getDefault(), "±%.1f m", accuracyMeters)
    }
}
