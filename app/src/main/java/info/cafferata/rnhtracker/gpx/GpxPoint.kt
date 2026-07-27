package info.cafferata.rnhtracker.gpx

import java.util.Date

/** One recorded trackpoint or waypoint. */
data class GpxPoint(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double? = null,
    val time: Date = Date(),
)
