package praeterii.radio.compose.screen

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.murgupluoglu.flagkit.FlagKit
import com.r.cohen.radiobrowserandroid.models.RadioBrowserStation
import praeterii.radio.R
import praeterii.radio.compose.station.NowPlayingBar
import praeterii.radio.compose.station.StationItem
import praeterii.radio.ui.theme.RadioTheme
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RadioScreen(
    stations: List<RadioBrowserStation>,
    currentCountryCode: String,
    currentMediaItem: MediaItem?,
    isPlaying: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onStationClick: (RadioBrowserStation) -> Unit,
    onToggleLocale: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onRetry: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                actions = {
                    val flagResId = FlagKit.getResId(currentCountryCode)
                    if (flagResId != 0) {
                        IconButton(onClick = onToggleLocale) {
                            Image(
                                painter = painterResource(flagResId),
                                contentDescription = "Toggle Locale",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = currentMediaItem != null && !isLandscape,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                if (currentMediaItem != null) {
                    NowPlayingBar(
                        mediaItem = currentMediaItem,
                        isPlaying = isPlaying,
                        onTogglePlayPause = onTogglePlayPause
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                ErrorState(
                    message = errorMessage,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        itemsIndexed(stations) { index, station ->
                            StationItem(
                                station = station,
                                onClick = { onStationClick(station) }
                            )
                            if (index < stations.lastIndex) {
                                FadingDivider()
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = isLandscape && currentMediaItem != null,
                        enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
                        exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
                    ) {
                        if (currentMediaItem != null) {
                            Surface(
                                tonalElevation = 8.dp,
                                shadowElevation = 8.dp,
                                modifier = Modifier
                                    .width(320.dp)
                                    .fillMaxHeight()
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    NowPlayingBar(
                                        mediaItem = currentMediaItem,
                                        isPlaying = isPlaying,
                                        onTogglePlayPause = onTogglePlayPause
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FadingDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 16.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
    )
}


@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview(device = TABLET)
@Composable
private fun RadioScreenPreview(
    @PreviewParameter(RadioScreenPreviewParameterProvider::class) state: RadioScreenPreviewState
) {
    RadioTheme {
        Surface {
            RadioScreen(
                stations = state.stations,
                currentCountryCode = state.currentCountryCode,
                currentMediaItem = state.currentlyPlayingStation?.let { station ->
                    MediaItem.Builder()
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(station.name)
                                .setArtworkUri(station.favicon.toUri())
                                .build()
                        )
                        .build()
                },
                isPlaying = state.isPlaying,
                isLoading = false,
                errorMessage = null,
                onStationClick = {},
                onToggleLocale = {},
                onTogglePlayPause = {},
                onRetry = {}
            )
        }
    }
}
