package info.cafferata.rnhtracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Rnh {
    val navy = Color(0xFF0B2E5C)
    val lightBlue = Color(0xFF3E8FC4)
    val orange = Color(0xFFF5A623)

    // Aliases matching the shared TrackerScreen's naming (stop = danger/red equivalent).
    val red = orange
    val blue = navy
}

@Composable
fun RnhTheme(content: @Composable () -> Unit) {
    val scheme = lightColorScheme(primary = Rnh.blue, secondary = Rnh.orange)
    MaterialTheme(colorScheme = scheme, content = content)
}
