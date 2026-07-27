package info.cafferata.rnhtracker.model

import java.io.File

data class GpxFileInfo(val file: File) {
    val name: String get() = file.nameWithoutExtension
    val modifiedDate: Long get() = file.lastModified()
    val fileSize: Long get() = file.length()
}
