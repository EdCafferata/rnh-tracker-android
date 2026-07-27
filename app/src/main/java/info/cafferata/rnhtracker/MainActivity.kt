package info.cafferata.rnhtracker

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import info.cafferata.rnhtracker.location.TrackingRepository
import info.cafferata.rnhtracker.location.TrackingService
import info.cafferata.rnhtracker.model.GpxFileInfo
import info.cafferata.rnhtracker.store.GpxFileRepository
import info.cafferata.rnhtracker.ui.screens.FileListScreen
import info.cafferata.rnhtracker.ui.screens.TrackerScreen
import info.cafferata.rnhtracker.ui.theme.RnhTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        org.osmdroid.config.Configuration.getInstance().userAgentValue = packageName

        setContent {
            RnhTheme {
                RnhApp()
            }
        }
    }
}

@Composable
private fun RnhApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val fileRepo = remember { GpxFileRepository(context) }
    val state by TrackingRepository.state.collectAsState()
    var showFileList by remember { mutableStateOf(false) }
    var files by remember { mutableStateOf(fileRepo.listFiles()) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
        if (granted[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            context.startForegroundService(Intent(context, TrackingService::class.java))
        }
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    if (showFileList) {
        FileListScreen(
            files = files,
            onBack = { showFileList = false },
            onShare = { info -> shareFile(context, info) },
            onDelete = { info -> fileRepo.delete(info); files = fileRepo.listFiles() },
        )
    } else {
        TrackerScreen(
            state = state,
            onToggleTracking = {
                if (state.isTracking) TrackingRepository.stop() else TrackingRepository.start()
            },
            onSave = {
                val gpx = TrackingRepository.session.exportToGpxString()
                fileRepo.save(gpx)
                TrackingRepository.reset()
                files = fileRepo.listFiles()
            },
            onOpenFiles = {
                files = fileRepo.listFiles()
                showFileList = true
            },
        )
    }
}

private fun shareFile(context: android.content.Context, info: GpxFileInfo) {
    val uri: Uri = FileProvider.getUriForFile(context, "info.cafferata.rnhtracker.fileprovider", info.file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        // "application/gpx+xml" is too specific — almost nothing registers for it
        // by exact MIME type. "*/*" lets the system show every generic file target
        // (Bluetooth, Files, Drive, mail attachments, ...), same as the iOS app's
        // plain UIActivityViewController(url:) share sheet.
        type = "*/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, null))
}
