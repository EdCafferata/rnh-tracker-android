package info.cafferata.rnhtracker.ui.map

import org.osmdroid.tileprovider.tilesource.XYTileSource

/** Builds an osmdroid [XYTileSource] from a [TileServer]'s `{s}/{z}/{x}/{y}` URL template. */
fun TileServer.toTileSource(): XYTileSource {
    val prefix = urlTemplate.substringBefore("{z}")
    val ending = urlTemplate.substringAfter("{y}")
    val hosts = subdomains.ifEmpty { listOf("") }
    val baseUrls = hosts.map { prefix.replace("{s}", it) }.toTypedArray()

    return XYTileSource(
        name,
        0,
        maximumZ,
        tileSize,
        ending,
        baseUrls,
        "© $displayName contributors",
    )
}
