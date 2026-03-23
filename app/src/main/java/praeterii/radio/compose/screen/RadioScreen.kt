package praeterii.radio.compose.screen

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import praeterii.radio.data.RadioCountry
import praeterii.radio.compose.country.CountryPickerSheet
import praeterii.radio.compose.commons.FadingDivider
import praeterii.radio.compose.commons.ErrorState
import praeterii.radio.compose.station.NowPlayingBarPortrait
import praeterii.radio.compose.station.NowPlayingBarLandscape
import praeterii.radio.compose.station.StationItem
import praeterii.radio.domain.model.RadioModel
import praeterii.radio.theme.RadioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RadioScreen(
    stations: List<RadioModel>,
    favoriteStationIds: Set<String>,
    countries: List<RadioCountry>,
    isCountriesLoading: Boolean,
    currentCountryCode: String,
    currentlyPlayingId: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    title: String,
    subtitle: String? = null,
    artworkUri: String?,
    showPlayerBar: Boolean,
    isPlaying: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onStationClick: (RadioModel) -> Unit,
    onToggleFavorite: (RadioModel) -> Unit,
    onOpenCountryPicker: () -> Unit,
    onCountrySelect: (RadioCountry) -> Unit,
    onTogglePlayPause: () -> Unit,
    onRetry: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var showCountryPicker by remember { mutableStateOf(false) }
    var isSearchVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    BackHandler(enabled = isSearchVisible) {
        isSearchVisible = false
        onSearchQueryChange("")
    }

    Scaffold(
        topBar = {
            RadioTopAppBar(
                isSearchVisible = isSearchVisible,
                onSearchToggle = { isSearchVisible = it },
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                currentCountryCode = currentCountryCode,
                onOpenCountryPicker = onOpenCountryPicker,
                onShowCountryPicker = { showCountryPicker = true },
                focusRequester = focusRequester
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showPlayerBar && !isLandscape,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                NowPlayingBarPortrait(
                    title = title,
                    artworkUri = artworkUri,
                    isPlaying = isPlaying,
                    isFavorite = favoriteStationIds.contains(currentlyPlayingId),
                    onToggleFavorite = {
                        stations.find { it.stationuuid == currentlyPlayingId }?.let { onToggleFavorite(it) }
                    },
                    onTogglePlayPause = onTogglePlayPause,
                    modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                    subtitle = subtitle
                )
            }
        }
    ) { paddingValues ->
        RadioContent(
            stations = stations,
            favoriteStationIds = favoriteStationIds,
            title = title,
            subtitle = subtitle,
            artworkUri = artworkUri,
            currentlyPlayingId = currentlyPlayingId,
            showPlayerBar = showPlayerBar,
            isPlaying = isPlaying,
            isLoading = isLoading,
            errorMessage = errorMessage,
            isLandscape = isLandscape,
            onStationClick = onStationClick,
            onToggleFavorite = onToggleFavorite,
            onTogglePlayPause = onTogglePlayPause,
            onRetry = onRetry,
            modifier = Modifier.padding(paddingValues)
        )

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

    LaunchedEffect(key1 = isSearchVisible) {
        if (isSearchVisible) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun RadioContent(
    stations: List<RadioModel>,
    favoriteStationIds: Set<String>,
    title: String,
    subtitle: String?,
    artworkUri: String?,
    currentlyPlayingId: String?,
    showPlayerBar: Boolean,
    isPlaying: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    isLandscape: Boolean,
    onStationClick: (RadioModel) -> Unit,
    onToggleFavorite: (RadioModel) -> Unit,
    onTogglePlayPause: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            errorMessage != null -> {
                ErrorState(
                    message = errorMessage,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        itemsIndexed(
                            items = stations,
                            key = { _, station -> station.stationuuid }
                        ) { index, station ->
                            StationItem(
                                station = station,
                                isFavorite = favoriteStationIds.contains(station.stationuuid),
                                onFavoriteToggle = { onToggleFavorite(station) },
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
                                NowPlayingBarLandscape(
                                    title = title,
                                    artworkUri = artworkUri,
                                    isPlaying = isPlaying,
                                    isFavorite = favoriteStationIds.contains(currentlyPlayingId),
                                    onToggleFavorite = {
                                        stations.find { it.stationuuid == currentlyPlayingId }?.let { onToggleFavorite(it) }
                                    },
                                    onTogglePlayPause = onTogglePlayPause,
                                    subtitle = subtitle
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
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
                favoriteStationIds = emptySet(),
                countries = emptyList(),
                isCountriesLoading = false,
                currentCountryCode = state.currentCountryCode,
                currentlyPlayingId = null,
                searchQuery = "",
                onSearchQueryChange = {},
                title = state.currentlyPlayingStation?.name ?: "Unknown station",
                artworkUri = null,
                isPlaying = state.isPlaying,
                isLoading = false,
                showPlayerBar = state.currentlyPlayingStation != null,
                errorMessage = null,
                onStationClick = {},
                onToggleFavorite = {},
                onOpenCountryPicker = {},
                onCountrySelect = {},
                onTogglePlayPause = {},
                onRetry = {}
            )
        }
    }
}
