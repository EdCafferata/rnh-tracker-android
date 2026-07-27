package info.cafferata.rnhtracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.cafferata.rnhtracker.model.GpxFileInfo
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListScreen(
    files: List<GpxFileInfo>,
    onBack: () -> Unit,
    onShare: (GpxFileInfo) -> Unit,
    onDelete: (GpxFileInfo) -> Unit,
) {
    var fileToDelete by remember { mutableStateOf<GpxFileInfo?>(null) }
    val dateFormat = remember { SimpleDateFormat("d MMM yyyy HH:mm", Locale("nl")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Opgeslagen tracks") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Terug") }
                },
            )
        },
    ) { padding ->
        if (files.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            ) {
                Text("Nog geen tracks opgeslagen")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(files, key = { it.file.path }) { info ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.InsertDriveFile, contentDescription = null)
                        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(info.name)
                            Text(dateFormat.format(info.modifiedDate), fontSize = 12.sp)
                        }
                        IconButton(onClick = { onShare(info) }) { Icon(Icons.Filled.Share, contentDescription = "Deel") }
                        IconButton(onClick = { fileToDelete = info }) { Icon(Icons.Filled.Delete, contentDescription = "Verwijder") }
                    }
                }
            }
        }
    }

    fileToDelete?.let { info ->
        AlertDialog(
            onDismissRequest = { fileToDelete = null },
            title = { Text("Track verwijderen?") },
            text = { Text("${info.name} wordt permanent verwijderd.") },
            confirmButton = { TextButton(onClick = { onDelete(info); fileToDelete = null }) { Text("Verwijder") } },
            dismissButton = { TextButton(onClick = { fileToDelete = null }) { Text("Annuleer") } },
        )
    }
}
