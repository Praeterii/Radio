package praeterii.radio.compose.screen

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    onStationClick: (RadioBrowserStation) -> Unit,
    onToggleLocale: () -> Unit,
    onTogglePlayPause: () -> Unit
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
            if (currentMediaItem != null && !isLandscape) {
                NowPlayingBar(
                    mediaItem = currentMediaItem,
                    isPlaying = isPlaying,
                    onTogglePlayPause = onTogglePlayPause
                )
            }
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                items(stations) { station ->
                    StationItem(
                        station = station,
                        onClick = { onStationClick(station) }
                    )
                }
            }

            if (isLandscape && currentMediaItem != null) {
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
                onStationClick = {},
                onToggleLocale = {},
                onTogglePlayPause = {}
            )
        }
    }
}
