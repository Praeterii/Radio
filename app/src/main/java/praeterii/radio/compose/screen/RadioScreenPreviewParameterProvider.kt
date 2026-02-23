package praeterii.radio.compose.screen

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import praeterii.radio.data.RadioStation
import praeterii.radio.domain.model.RadioModel

class RadioScreenPreviewParameterProvider : PreviewParameterProvider<RadioScreenPreviewState> {
    override val values = sequenceOf(
        RadioScreenPreviewState(
            stations = listOf(
                RadioModel(
                    stationuuid = "uuid1",
                    name = "RMF FM",
                    url = "https://example.com/1",
                    favicon = "https://example.com/favicon.ico",
                ),
                RadioModel(
                    stationuuid = "uuid2",
                    name = "Radio ZET",
                    url = "https://example.com/2",
                    favicon = "https://example.com/favicon2.ico",
                )
            ),
            currentCountryCode = "PL",
            currentlyPlayingStation = RadioModel(
                stationuuid = "uuid1",
                name = "RMF FM",
                url = "https://example.com/1",
                favicon = "https://example.com/favicon.ico",
            ),
            isPlaying = true
        ),
        RadioScreenPreviewState(
            stations = listOf(
                RadioModel(
                    stationuuid = "uuid3",
                    name = "BBC Radio 1",
                    url = "https://example.com/3",
                    favicon = "https://example.com/favicon3.ico",
                )
            ),
            currentCountryCode = "US",
            isPlaying = true
        )
    )
}

data class RadioScreenPreviewState(
    val stations: List<RadioModel>,
    val currentCountryCode: String,
    val currentlyPlayingStation: RadioModel? = null,
    val isPlaying: Boolean = false
)
