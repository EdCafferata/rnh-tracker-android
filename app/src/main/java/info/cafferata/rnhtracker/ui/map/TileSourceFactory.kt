package info.cafferata.rnhtracker.ui.map

import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.MapTileIndex

/**
 * Builds an osmdroid tile source from a [TileServer]'s URL template.
 *
 * Substitueert `{s}` (subdomein), `{z}`, `{x}` en `{y}` in de template, in WELKE volgorde
 * dan ook. Daardoor werken zowel `{z}/{x}/{y}`-servers (OSM, OpenTopoMap, OpenSeaMap) als
 * `{z}/{y}/{x}`-servers (Esri) met exact dezelfde config — zonder dat de vaste x/y-volgorde
 * van osmdroid's standaard-URL-opbouw in de weg zit.
 */
fun TileServer.toTileSource(): XYTileSource {
    val hosts = subdomains
    val template = urlTemplate
    return object : XYTileSource(
        name,
        0,
        maximumZ,
        tileSize,
        "",
        arrayOf(""), // base-URL's niet gebruikt: getTileURLString is volledig overschreven
        "© $displayName",
    ) {
        private var subIndex = 0

        override fun getTileURLString(pMapTileIndex: Long): String {
            val z = MapTileIndex.getZoom(pMapTileIndex)
            val x = MapTileIndex.getX(pMapTileIndex)
            val y = MapTileIndex.getY(pMapTileIndex)
            val s = if (hosts.isEmpty()) "" else hosts[Math.floorMod(subIndex++, hosts.size)]
            return template
                .replace("{s}", s)
                .replace("{z}", z.toString())
                .replace("{x}", x.toString())
                .replace("{y}", y.toString())
        }
    }
}
