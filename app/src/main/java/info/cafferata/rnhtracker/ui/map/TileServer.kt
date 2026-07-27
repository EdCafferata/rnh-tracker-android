package info.cafferata.rnhtracker.ui.map

/**
 * Supported base map tile servers. Port of the iOS app's `GPXTileServer`, minus Apple Maps
 * and Apple Satellite — there's no MapKit equivalent on Android, so OpenStreetMap is the
 * default base layer instead.
 */
enum class TileServer(
    val displayName: String,
    val urlTemplate: String,
    val subdomains: List<String>,
    val maximumZ: Int,
    val tileSize: Int,
    /** true for overlays that draw on top of a base layer rather than replacing it. */
    val isOverlay: Boolean = false,
) {
    OPEN_STREET_MAP(
        displayName = "OpenStreetMap",
        urlTemplate = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        subdomains = listOf("a", "b", "c"),
        maximumZ = 19,
        tileSize = 256,
    ),
    CARTO_DB(
        displayName = "Carto DB",
        urlTemplate = "https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}.png",
        subdomains = listOf("a", "b", "c"),
        maximumZ = 21,
        tileSize = 256,
    ),
    CARTO_DB_RETINA(
        displayName = "Carto DB (Retina)",
        urlTemplate = "https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}@2x.png",
        subdomains = listOf("a", "b", "c"),
        maximumZ = 21,
        tileSize = 512,
    ),
    OPEN_TOPO_MAP(
        displayName = "OpenTopoMap",
        urlTemplate = "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
        subdomains = listOf("a", "b", "c"),
        maximumZ = 17,
        tileSize = 256,
    ),
    CARTO_DB_DARK(
        displayName = "Carto DB Dark Matter",
        urlTemplate = "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}.png",
        subdomains = listOf("a", "b", "c"),
        maximumZ = 21,
        tileSize = 256,
    ),
    OPEN_SEA_MAP(
        displayName = "OpenSeaMap",
        urlTemplate = "https://tiles.openseamap.org/seamark/{z}/{x}/{y}.png",
        subdomains = emptyList(),
        maximumZ = 18,
        tileSize = 256,
        isOverlay = true,
    ),
}
