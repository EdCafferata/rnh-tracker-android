package info.cafferata.rnhtracker.ui.map

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import info.cafferata.rnhtracker.gpx.GpxPoint
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay

private val RNH_ROUTE_START = GeoPoint(52.4630, 4.5480)

@Composable
fun OsmMapView(
    baseLayer: TileServer,
    showSeaMarks: Boolean,
    trackPoints: List<GpxPoint>,
    currentLocation: GpxPoint?,
    followUser: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val trackLine = remember { Polyline().apply { outlinePaint.color = Color.RED; outlinePaint.strokeWidth = 6f } }
    val positionMarker = remember { Marker(mapView) }
    var seaMarksOverlay by remember { androidx.compose.runtime.mutableStateOf<TilesOverlay?>(null) }

    DisposableEffect(Unit) {
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(13.0)
        mapView.controller.setCenter(RNH_ROUTE_START)
        mapView.overlays.add(trackLine)
        positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        mapView.overlays.add(positionMarker)
        onDispose { mapView.onDetach() }
    }

    LaunchedEffect(baseLayer) {
        mapView.setTileSource(baseLayer.toTileSource())
        mapView.invalidate()
    }

    LaunchedEffect(showSeaMarks) {
        seaMarksOverlay?.let { mapView.overlayManager.remove(it) }
        seaMarksOverlay = null
        if (showSeaMarks) {
            val provider = org.osmdroid.tileprovider.MapTileProviderBasic(context)
            provider.tileSource = TileServer.OPEN_SEA_MAP.toTileSource()
            val overlay = TilesOverlay(provider, context)
            overlay.loadingBackgroundColor = Color.TRANSPARENT
            mapView.overlayManager.add(overlay)
            seaMarksOverlay = overlay
        }
        mapView.invalidate()
    }

    LaunchedEffect(trackPoints.size) {
        trackLine.setPoints(trackPoints.map { GeoPoint(it.latitude, it.longitude) })
        mapView.invalidate()
    }

    LaunchedEffect(currentLocation, followUser) {
        val loc = currentLocation ?: return@LaunchedEffect
        val geoPoint = GeoPoint(loc.latitude, loc.longitude)
        positionMarker.position = geoPoint
        if (followUser) mapView.controller.animateTo(geoPoint)
        mapView.invalidate()
    }

    AndroidView(factory = { mapView }, modifier = modifier.fillMaxSize())
}
