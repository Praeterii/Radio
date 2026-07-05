package praeterii.radio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import praeterii.radio.compose.screen.RadioScreen
import praeterii.radio.compose.screen.settings.SettingsScreen
import praeterii.radio.theme.RadioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RadioTheme {
                Surface {
                    RadioApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RadioApp(viewModel: RadioViewModel = viewModel()) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.loadStations()
    }

    NavHost(navController = navController, startDestination = "radio") {
        composable(
            route = "radio",
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            }
        ) {
            val favoriteStationIds by viewModel.favoriteStationIds.collectAsState()
            
            val onSearchQueryChange = remember(viewModel) { { query: String -> viewModel.onSearchQueryChange(query) } }
            val onStationClick = remember(viewModel) { { station: praeterii.radio.domain.model.RadioModel -> viewModel.playStation(station) } }
            val onToggleFavorite = remember(viewModel) { { station: praeterii.radio.domain.model.RadioModel -> viewModel.toggleFavorite(station) } }
            val onTogglePlayPause = remember(viewModel) { { viewModel.togglePlayPause() } }
            val onLoadMore = remember(viewModel) { { viewModel.loadMoreStations() } }
            val onRetry = remember(viewModel) { { viewModel.loadStations() } }
            val onSettingsClick = remember { { navController.navigate("settings") } }

            RadioScreen(
                stations = viewModel.stations,
                favoriteStationIds = favoriteStationIds,
                currentlyPlayingId = viewModel.currentMediaItem?.mediaId,
                searchQuery = viewModel.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                title = viewModel.currentMetadata?.artist?.toString() ?: stringResource(R.string.app_name),
                subtitle = viewModel.currentMetadata?.title?.toString(),
                artworkUri = viewModel.currentMetadata?.artworkUri?.toString(),
                isPlaying = viewModel.isPlaying.value,
                isLoading = viewModel.isLoading,
                isLoadingMore = viewModel.isLoadingMore,
                showPlayerBar = viewModel.isNowPlayingBarVisible,
                errorMessage = viewModel.errorMessage,
                onStationClick = onStationClick,
                onToggleFavorite = onToggleFavorite,
                onTogglePlayPause = onTogglePlayPause,
                onSettingsClick = onSettingsClick,
                onLoadMore = onLoadMore,
                onRetry = onRetry,
            )
        }
        composable(
            route = "settings",
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }
        ) {
            val settingsViewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                countries = settingsViewModel.countries,
                isCountriesLoading = settingsViewModel.isCountriesLoading,
                currentCountryCode = settingsViewModel.currentCountryCode,
                stopPlaybackOnTaskRemoved = settingsViewModel.stopPlaybackOnTaskRemoved,
                onStopPlaybackOnTaskRemovedChange = { settingsViewModel.updateStopPlaybackOnTaskRemoved(it) },
                onOpenCountryPicker = { settingsViewModel.loadCountries() },
                onCountrySelect = { settingsViewModel.selectCountry(it) },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
