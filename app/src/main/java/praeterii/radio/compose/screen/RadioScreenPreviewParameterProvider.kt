package praeterii.radio.compose.screen

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.r.cohen.radiobrowserandroid.models.RadioBrowserStation

class RadioScreenPreviewParameterProvider : PreviewParameterProvider<RadioScreenPreviewState> {
    override val values = sequenceOf(
        RadioScreenPreviewState(
            stations = listOf(
                RadioBrowserStation(
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
                RadioBrowserStation(
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
            currentlyPlayingStation = RadioBrowserStation(
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
                RadioBrowserStation(
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
    val stations: List<RadioBrowserStation>,
    val currentCountryCode: String,
    val currentlyPlayingStation: RadioBrowserStation? = null,
    val isPlaying: Boolean = false
)
