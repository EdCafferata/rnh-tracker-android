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
    // CARTO_DB serveert sinds okt 2026 een Esri keyless basemap (Carto heeft z'n gratis
    // keyless basemaps afgeknepen: "API KEY REQUIRED"). Esri = keyless, geen account.
    // De grijze Esri-canvas-lagen ("Licht grijs"/"Donker grijs") zijn okt 2026 verwijderd
    // (op verzoek Ed): alleen tegels tot z16 en te weinig beeld boven water.
    CARTO_DB(
        displayName = "Wegenkaart (Esri)",
        urlTemplate = "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}",
        subdomains = emptyList(),
        maximumZ = 19,
        tileSize = 256,
    ),
    OPEN_TOPO_MAP(
        displayName = "OpenTopoMap",
        urlTemplate = "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
        subdomains = listOf("a", "b", "c"),
        maximumZ = 17,
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
