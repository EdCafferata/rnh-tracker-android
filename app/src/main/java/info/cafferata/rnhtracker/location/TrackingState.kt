package info.cafferata.rnhtracker.location

import info.cafferata.rnhtracker.gpx.GpxPoint

data class TrackingState(
    val isTracking: Boolean = false,
    val currentLocation: GpxPoint? = null,
    val trackPoints: List<GpxPoint> = emptyList(),
    val totalDistanceMeters: Double = 0.0,
    val speedMetersPerSecond: Float = 0f,
)
