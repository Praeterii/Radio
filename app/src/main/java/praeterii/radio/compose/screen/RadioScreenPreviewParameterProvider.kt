package praeterii.radio.compose.screen

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import praeterii.radio.model.RadioStation

class RadioScreenPreviewParameterProvider : PreviewParameterProvider<RadioScreenPreviewState> {
    override val values = sequenceOf(
        RadioScreenPreviewState(
            stations = listOf(
                RadioStation(
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
                RadioStation(
                    stationuuid = "uuid2",
                    name = "Radio ZET",
                    url = "https://example.com/2",
                    url_resolved = "https://example.com/2",
                    homepage = "https://radiozet.pl",
                    favicon = "https://example.com/favicon2.ico",
                    tags = "pop, talk",
                    country = "Poland",
                    language = "polish"
                )
            ),
            currentCountryCode = "PL",
            currentlyPlayingStation = RadioStation(
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
            isPlaying = true
        ),
        RadioScreenPreviewState(
            stations = listOf(
                RadioStation(
                    stationuuid = "uuid3",
                    name = "BBC Radio 1",
                    url = "https://example.com/3",
                    url_resolved = "https://example.com/3",
                    homepage = "https://bbc.co.uk",
                    favicon = "https://example.com/favicon3.ico",
                    tags = "pop, rock",
                    country = "United Kingdom",
                    language = "english"
                )
            ),
            currentCountryCode = "US",
            isPlaying = true
        )
    )
}

data class RadioScreenPreviewState(
    val stations: List<RadioStation>,
    val currentCountryCode: String,
    val currentlyPlayingStation: RadioStation? = null,
    val isPlaying: Boolean = false
)
