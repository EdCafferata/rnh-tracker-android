package info.cafferata.rnhtracker.store

import android.content.Context
import info.cafferata.rnhtracker.model.GpxFileInfo
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/** Saves, lists, and deletes recorded `.gpx` files in the app's private storage. */
class GpxFileRepository(context: Context) {

    private val tracksDir: File = context.filesDir

    fun listFiles(): List<GpxFileInfo> =
        tracksDir.listFiles { f -> f.isFile && f.extension.equals("gpx", ignoreCase = true) }
            ?.map { GpxFileInfo(it) }
            ?.sortedByDescending { it.modifiedDate }
            ?: emptyList()

    fun save(gpxContents: String, suggestedName: String? = null): File {
        val name = suggestedName ?: defaultFilename()
        val file = File(tracksDir, "$name.gpx")
        file.writeText(gpxContents)
        return file
    }

    fun delete(info: GpxFileInfo) {
        info.file.delete()
    }

    fun defaultFilename(): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        return "RNH_${fmt.format(java.util.Date())}"
    }
}
