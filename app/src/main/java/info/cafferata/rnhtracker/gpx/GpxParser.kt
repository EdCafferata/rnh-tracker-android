package info.cafferata.rnhtracker.gpx

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

data class ParsedGpx(val waypoints: List<GpxPoint>, val segments: List<List<GpxPoint>>) {
    val allTrackPoints: List<GpxPoint> get() = segments.flatten()

    val totalDistanceMeters: Double
        get() {
            var total = 0.0
            for (segment in segments) {
                for (i in 1 until segment.size) {
                    val a = segment[i - 1]
                    val b = segment[i]
                    val results = FloatArray(1)
                    android.location.Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, results)
                    total += results[0]
                }
            }
            return total
        }
}

/** Minimal GPX 1.1 reader — just enough to redisplay a track (waypoints + track segments) on the map. */
object GpxParser {

    fun parse(xml: String): ParsedGpx {
        val waypoints = mutableListOf<GpxPoint>()
        val segments = mutableListOf<List<GpxPoint>>()
        var currentSegment: MutableList<GpxPoint>? = null

        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xml))

        var currentTag: String? = null
        var lat = 0.0
        var lon = 0.0
        var ele: Double? = null

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    val name = parser.name
                    currentTag = name
                    when (name) {
                        "wpt", "trkpt" -> {
                            lat = parser.getAttributeValue(null, "lat")?.toDoubleOrNull() ?: 0.0
                            lon = parser.getAttributeValue(null, "lon")?.toDoubleOrNull() ?: 0.0
                            ele = null
                        }
                        "trkseg" -> currentSegment = mutableListOf()
                    }
                }
                XmlPullParser.TEXT -> {
                    if (currentTag == "ele") {
                        ele = parser.text?.toDoubleOrNull()
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (parser.name) {
                        "wpt" -> waypoints.add(GpxPoint(lat, lon, ele))
                        "trkpt" -> currentSegment?.add(GpxPoint(lat, lon, ele))
                        "trkseg" -> currentSegment?.let { segments.add(it) }
                    }
                    currentTag = null
                }
            }
            event = parser.next()
        }
        return ParsedGpx(waypoints, segments)
    }
}
