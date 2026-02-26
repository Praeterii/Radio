package praeterii.radio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import praeterii.radio.compose.screen.RadioScreen
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
    LaunchedEffect(Unit) {
        viewModel.loadStations()
    }
    RadioScreen(
        stations = viewModel.stations,
        countries = viewModel.countries,
        isCountriesLoading = viewModel.isCountriesLoading,
        currentCountryCode = viewModel.currentCountryCode,
        title = viewModel.currentMediaItem?.mediaMetadata?.artist?.toString() ?: "Unknown Station",
        artworkUri = viewModel.currentMediaItem?.mediaMetadata?.artworkUri?.toString(),
        isPlaying = viewModel.isPlaying,
        isLoading = viewModel.isLoading,
        showPlayerBar = viewModel.isNowPlayingBarVisible,
        errorMessage = viewModel.errorMessage,
        onStationClick = { viewModel.playStation(it) },
        onOpenCountryPicker = { viewModel.loadCountries() },
        onCountrySelect = { viewModel.selectCountry(it) },
        onTogglePlayPause = { viewModel.togglePlayPause() },
        onRetry = { viewModel.loadStations() }
    )
}
