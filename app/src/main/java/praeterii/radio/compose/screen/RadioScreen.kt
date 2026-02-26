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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.murgupluoglu.flagkit.FlagKit
import praeterii.radio.data.RadioCountry
import praeterii.radio.R
import praeterii.radio.compose.country.CountryPickerSheet
import praeterii.radio.compose.FadingDivider
import praeterii.radio.compose.station.NowPlayingBar
import praeterii.radio.compose.station.StationItem
import praeterii.radio.domain.model.RadioModel
import praeterii.radio.theme.RadioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RadioScreen(
    stations: List<RadioModel>,
    countries: List<RadioCountry>,
    isCountriesLoading: Boolean,
    currentCountryCode: String,
    title: String,
    artworkUri: String?,
    showPlayerBar: Boolean,
    isPlaying: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onStationClick: (RadioModel) -> Unit,
    onOpenCountryPicker: () -> Unit,
    onCountrySelect: (RadioCountry) -> Unit,
    onTogglePlayPause: () -> Unit,
    onRetry: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var showCountryPicker by remember { mutableStateOf(false) }

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
                        IconButton(onClick = {
                            onOpenCountryPicker()
                            showCountryPicker = true
                        }) {
                            Image(
                                painter = painterResource(flagResId),
                                contentDescription = "Select Country",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showPlayerBar && !isLandscape,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                NowPlayingBar(
                    title = title,
                    artworkUri = artworkUri,
                    isPlaying = isPlaying,
                    onTogglePlayPause = onTogglePlayPause
                )
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
                        visible = isLandscape && showPlayerBar,
                        enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
                        exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
                    ) {
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
                                        title = title,
                                        artworkUri = artworkUri,
                                        isPlaying = isPlaying,
                                        onTogglePlayPause = onTogglePlayPause
                                    )
                                }
                            }
                    }
                }
            }
        }

        if (showCountryPicker) {
            CountryPickerSheet(
                countries = countries,
                isLoading = isCountriesLoading,
                onCountrySelect = {
                    onCountrySelect(it)
                    showCountryPicker = false
                },
                onDismissRequest = { showCountryPicker = false }
            )
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
                countries = emptyList(),
                isCountriesLoading = false,
                currentCountryCode = state.currentCountryCode,
                title = state.currentlyPlayingStation?.name ?: "Unknown station",
                artworkUri = null,
                isPlaying = state.isPlaying,
                isLoading = false,
                showPlayerBar = state.currentlyPlayingStation != null,
                errorMessage = null,
                onStationClick = {},
                onOpenCountryPicker = {},
                onCountrySelect = {},
                onTogglePlayPause = {},
                onRetry = {}
            )
        }
    }
}
