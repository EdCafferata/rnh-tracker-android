package info.cafferata.rnhtracker.gpx

import android.location.Location
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Handles the actual logging of waypoints and trackpoints, and exporting the recorded
 * session as a GPX string. Port of the iOS app's `GPXSession`.
 */
class GpxSession {

    val waypoints = mutableListOf<GpxPoint>()

    /** Segments recorded so far in this session (each a pause/resume boundary). */
    val trackSegments = mutableListOf<List<GpxPoint>>()

    /** Points in the segment currently being recorded. */
    var currentSegment = mutableListOf<GpxPoint>()
        private set

    var totalTrackedDistance = 0.0
        private set
    var currentSegmentDistance = 0.0
        private set

    fun addWaypoint(point: GpxPoint) {
        waypoints.add(point)
    }

    fun addPointToCurrentSegment(location: Location) {
        val point = GpxPoint(
            latitude = location.latitude,
            longitude = location.longitude,
            elevation = if (location.hasAltitude()) location.altitude else null,
        )
        val previous = currentSegment.lastOrNull()
        currentSegment.add(point)
        if (previous != null) {
            val results = FloatArray(1)
            Location.distanceBetween(previous.latitude, previous.longitude, point.latitude, point.longitude, results)
            totalTrackedDistance += results[0]
            currentSegmentDistance += results[0]
        }
    }

    /** Appends the current segment to trackSegments and starts a fresh one. */
    fun startNewTrackSegment() {
        if (currentSegment.isNotEmpty()) {
            trackSegments.add(currentSegment)
            currentSegment = mutableListOf()
            currentSegmentDistance = 0.0
        }
    }

    fun reset() {
        trackSegments.clear()
        currentSegment = mutableListOf()
        waypoints.clear()
        totalTrackedDistance = 0.0
        currentSegmentDistance = 0.0
    }

    fun exportToGpxString(creator: String = "RNH Tracker for Android"): String {
        val allSegments = trackSegments + if (currentSegment.isNotEmpty()) listOf(currentSegment) else emptyList()
        val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<gpx version=\"1.1\" creator=\"$creator\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n")
        for (wp in waypoints) {
            sb.append(pointXml("wpt", wp, iso))
        }
        if (allSegments.isNotEmpty()) {
            sb.append("  <trk>\n")
            for (segment in allSegments) {
                sb.append("    <trkseg>\n")
                for (pt in segment) {
                    sb.append(pointXml("trkpt", pt, iso, indent = "      "))
                }
                sb.append("    </trkseg>\n")
            }
            sb.append("  </trk>\n")
        }
        sb.append("</gpx>\n")
        return sb.toString()
    }

    private fun pointXml(tag: String, p: GpxPoint, iso: SimpleDateFormat, indent: String = "  "): String {
        val sb = StringBuilder()
        sb.append("$indent<$tag lat=\"${p.latitude}\" lon=\"${p.longitude}\">\n")
        if (p.elevation != null) sb.append("$indent  <ele>${p.elevation}</ele>\n")
        sb.append("$indent  <time>${iso.format(p.time)}</time>\n")
        sb.append("$indent</$tag>\n")
        return sb.toString()
    }
}
