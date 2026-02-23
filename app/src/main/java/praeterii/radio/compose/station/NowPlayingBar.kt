package praeterii.radio.compose.station

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import praeterii.radio.R
import praeterii.radio.theme.RadioTheme

@Composable
internal fun NowPlayingBar(
    mediaItem: MediaItem,
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        NowPlayingLandscape(
            mediaItem = mediaItem,
            isPlaying = isPlaying,
            onTogglePlayPause = onTogglePlayPause,
            modifier = modifier
        )
    } else {
        NowPlayingPortrait(
            mediaItem = mediaItem,
            isPlaying = isPlaying,
            onTogglePlayPause = onTogglePlayPause,
            modifier = modifier
        )
    }
}

@Composable
private fun NowPlayingPortrait(
    mediaItem: MediaItem,
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val metadata = mediaItem.mediaMetadata
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            StationFavicon(
                artworkUri = metadata.artworkUri?.toString(),
                size = 48.dp,
                clipShape = MaterialTheme.shapes.small
            )

            Text(
                text = metadata.title?.toString() ?: "Unknown Station",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            PlayPauseButton(
                isPlaying = isPlaying,
                onClick = onTogglePlayPause
            )
        }
    }
}

@Composable
private fun NowPlayingLandscape(
    mediaItem: MediaItem,
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val metadata = mediaItem.mediaMetadata
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(16.dp)
            .fillMaxHeight()
    ) {
        StationFavicon(
            artworkUri = metadata.artworkUri?.toString(),
            size = 220.dp,
            clipShape = MaterialTheme.shapes.medium,
            padding = 24.dp
        )

        Text(
            text = metadata.title?.toString() ?: "Unknown Station",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
        )

        PlayPauseButton(
            isPlaying = isPlaying,
            onClick = onTogglePlayPause,
            modifier = Modifier
                .padding(top = 16.dp)
                .size(64.dp),
            iconSize = 48.dp
        )
    }
}

@Composable
private fun StationFavicon(
    artworkUri: String?,
    size: androidx.compose.ui.unit.Dp,
    clipShape: androidx.compose.ui.graphics.Shape,
    modifier: Modifier = Modifier,
    padding: androidx.compose.ui.unit.Dp = 8.dp
) {
    val placeholderTint = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(artworkUri)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(clipShape)
    ) {
        val state = painter.state
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            Icon(
                painter = painterResource(R.drawable.image_24px),
                contentDescription = null,
                tint = placeholderTint,
                modifier = Modifier
                    .size(size)
                    .padding(padding)
            )
        } else {
            SubcomposeAsyncImageContent()
        }
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 24.dp
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(
                id = if (isPlaying) R.drawable.pause_circle_24px else R.drawable.play_circle_24px
            ),
            contentDescription = if (isPlaying) "Pause" else "Play",
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview(device = TABLET, showBackground = true)
@Preview(device = TABLET, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NowPlayingBarPreview() {
    RadioTheme {
        NowPlayingBar(
            mediaItem = MediaItem.Builder()
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("RMF FM")
                        .build()
                )
                .build(),
            isPlaying = true,
            onTogglePlayPause = {}
        )
    }
}
