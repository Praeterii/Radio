package praeterii.radio.compose.station

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.r.cohen.radiobrowserandroid.models.RadioBrowserStation
import praeterii.radio.ui.theme.RadioTheme
import androidx.compose.material3.Icon
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme

@Composable
internal fun StationItem(
    station: RadioBrowserStation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        val placeholderTint = if (isSystemInDarkTheme()) {
            // In dark mode use the full onSurface color for contrast
            MaterialTheme.colorScheme.onSurface
        } else {
            // In light mode use a slightly muted onSurface to look like a placeholder
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        }

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(station.favicon)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .padding(end = 8.dp)
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                // Show a tinted icon while loading or on error
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = placeholderTint,
                    modifier = Modifier
                        .size(32.dp)
                )
            } else {
                SubcomposeAsyncImageContent(
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        }
        Text(text = station.name)
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun StationItemPreview() {
    RadioTheme {
        Surface {
            StationItem(
                station = RadioBrowserStation(
                    stationuuid = "uuid1",
                    name = "RMF FM",
                    url = "https://example.com/1",
                    url_resolved = "https://example.com/1",
                    homepage = "https://rmf.fm",
                    favicon = "https://example.com/favicon.ico",
                    tags = "pop, news",
                    country = "Poland",
                    language = "polish"
                ),
                onClick = {}
            )
        }
    }
}