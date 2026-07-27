package info.cafferata.rnhtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.cafferata.rnhtracker.location.TrackingState
import info.cafferata.rnhtracker.ui.map.OsmMapView
import info.cafferata.rnhtracker.ui.map.TileServer
import info.cafferata.rnhtracker.ui.theme.Rnh
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    state: TrackingState,
    onToggleTracking: () -> Unit,
    onSave: () -> Unit,
    onOpenFiles: () -> Unit,
) {
    var baseLayer by remember { mutableStateOf(TileServer.OPEN_STREET_MAP) }
    var showSeaMarks by remember { mutableStateOf(false) }
    var followUser by remember { mutableStateOf(true) }
    var showLayerMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        OsmMapView(
            baseLayer = baseLayer,
            showSeaMarks = showSeaMarks,
            trackPoints = state.trackPoints,
            currentLocation = state.currentLocation,
            followUser = followUser,
            modifier = Modifier.fillMaxSize(),
        )

        // Top bar: file list + layer switcher + sea marks + follow toggle.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(12.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        ) {
            RoundIconButton(icon = Icons.Filled.Folder, contentDescription = "Bestanden", onClick = onOpenFiles)

            Row {
                Box {
                    RoundIconButton(icon = Icons.Filled.Layers, contentDescription = "Kaartlaag", onClick = { showLayerMenu = true })
                    DropdownMenu(expanded = showLayerMenu, onDismissRequest = { showLayerMenu = false }) {
                        TileServer.entries.filter { !it.isOverlay }.forEach { server ->
                            DropdownMenuItem(text = { Text(server.displayName) }, onClick = { baseLayer = server; showLayerMenu = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                RoundIconButton(
                    icon = Icons.Filled.Waves,
                    contentDescription = "Vaarwegmarkeringen",
                    tint = if (showSeaMarks) Rnh.blue else Color.Gray,
                    onClick = { showSeaMarks = !showSeaMarks },
                )
                Spacer(modifier = Modifier.size(8.dp))
                RoundIconButton(
                    icon = Icons.Filled.MyLocation,
                    contentDescription = "Volg locatie",
                    tint = if (followUser) Rnh.blue else Color.Gray,
                    onClick = { followUser = !followUser },
                )
            }
        }

        // Bottom bar: distance/speed readout + start/stop/save.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
        ) {
            StatsCard(state)
            Spacer(modifier = Modifier.size(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (state.isTracking) Rnh.red else Rnh.blue, RoundedCornerShape(28.dp))
                        .clickable(onClick = onToggleTracking)
                        .padding(vertical = 16.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                ) {
                    Text(
                        if (state.isTracking) "STOP TRACKING" else "START TRACKING",
                        color = Color.White,
                        fontSize = 15.sp,
                    )
                }
                if (!state.isTracking && state.trackPoints.isNotEmpty()) {
                    IconButton(
                        onClick = onSave,
                        modifier = Modifier.size(52.dp).background(MaterialTheme.colorScheme.surface, CircleShape),
                    ) { Icon(Icons.Filled.Save, contentDescription = "Opslaan", tint = Rnh.blue) }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(state: TrackingState) {
    val km = state.totalDistanceMeters / 1000.0
    val kmh = state.speedMetersPerSecond * 3.6
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
    ) {
        StatItem(label = "AFSTAND", value = "%.2f km".format(km))
        StatItem(label = "SNELHEID", value = "%.1f km/u".format(kmh))
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp)
    }
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: Color = Color.Black,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(44.dp).background(Color.White, CircleShape),
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint)
    }
}
