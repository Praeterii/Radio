package praeterii.radio.compose.station

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import praeterii.radio.R
import praeterii.radio.domain.model.RadioModel
import praeterii.radio.theme.RadioTheme

@Composable
internal fun StationItem(
    station: RadioModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        val placeholderTint = if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        }

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(station.favicon)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(end = 12.dp)
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                Icon(
                    painter = painterResource(id = R.drawable.image_24px),
                    contentDescription = null,
                    tint = placeholderTint,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                SubcomposeAsyncImageContent(
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (station.tags.isNotEmpty()) {
                Text(
                    text = station.tags,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun StationItemPreview() {
    RadioTheme {
        Surface {
            StationItem(
                station = RadioModel(
                    stationuuid = "uuid1",
                    name = "RMF FM",
                    url = "https://example.com/1",
                    favicon = "https://example.com/favicon.ico",
                    tags = "pop, rock, news, hits"
                ),
                onClick = {}
            )
        }
    }
}
