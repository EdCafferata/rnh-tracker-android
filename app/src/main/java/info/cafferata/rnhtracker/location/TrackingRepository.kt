package info.cafferata.rnhtracker.location

import android.location.Location
import info.cafferata.rnhtracker.gpx.GpxPoint
import info.cafferata.rnhtracker.gpx.GpxSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Process-wide holder for the recording session, shared between [TrackingService] (which
 * feeds it GPS updates) and the UI (which observes [state]). Same single-process ownership
 * model as the iOS app's `GPXMapView.session`, just without a view to hang it off of.
 */
object TrackingRepository {

    val session = GpxSession()

    private val _state = MutableStateFlow(TrackingState())
    val state = _state.asStateFlow()

    fun start() {
        _state.value = _state.value.copy(isTracking = true)
    }

    fun stop() {
        session.startNewTrackSegment()
        _state.value = _state.value.copy(isTracking = false)
    }

    /** Called by [TrackingService] on every GPS fix — always updates the live position, and
     * additionally records the point into the session while tracking is on. */
    fun onLocation(location: Location) {
        val point = GpxPoint(location.latitude, location.longitude, location.altitude)
        if (_state.value.isTracking) {
            session.addPointToCurrentSegment(location)
        }
        _state.value = _state.value.copy(
            currentLocation = point,
            trackPoints = session.currentSegment.toList(),
            totalDistanceMeters = session.totalTrackedDistance,
            speedMetersPerSecond = if (location.hasSpeed()) location.speed else 0f,
        )
    }

    fun reset() {
        session.reset()
        _state.value = TrackingState()
    }
}
