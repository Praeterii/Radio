@file:Suppress("unused")

package praeterii.radio

import android.app.Application
import android.content.ComponentName
import androidx.annotation.Keep
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import praeterii.radio.repository.RadioStationsRepository
import praeterii.radio.data.RadioStationOrder
import praeterii.radio.data.RadioCountry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import praeterii.radio.domain.model.RadioModel
import praeterii.radio.playback.PlaybackService
import praeterii.radio.repository.LocaleRepository

@Keep
@Suppress("Unused")
class RadioViewModel(application: Application) : AndroidViewModel(application) {
    private val api by lazy { RadioStationsRepository() }
    private val localeRepository by lazy { LocaleRepository(application) }
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    var stations by mutableStateOf<List<RadioModel>>(emptyList())
    var currentCountryCode by mutableStateOf(localeRepository.getCurrentCountryCode())
        private set

    var countries by mutableStateOf<List<RadioCountry>>(emptyList())
        private set
    var isCountriesLoading by mutableStateOf(false)
        private set

    var currentMediaItem by mutableStateOf<MediaItem?>(null)
        private set
    var isPlaying by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    var isNowPlayingBarVisible by mutableStateOf(false)
        private set

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            currentMediaItem = mediaItem
        }

        override fun onIsPlayingChanged(playing: Boolean) {
            isPlaying = playing
        }
    }

    init {
        val sessionToken =
            SessionToken(application, ComponentName(application, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
        controllerFuture?.addListener({
            controller?.let {
                it.addListener(playerListener)
                currentMediaItem = it.currentMediaItem
                isPlaying = it.isPlaying
            }
        }, application.mainExecutor)
    }

    fun loadStations() {
        errorMessage = null
        isLoading = true
        api.getStationsByCountry(
            countryCode = currentCountryCode,
            limit = 1000,
            order = RadioStationOrder.CLICKCOUNT,
            onSuccess = { result ->
                viewModelScope.launch(Dispatchers.Main) {
                    stations = result
                    isLoading = false
                }
            },
            onFail = { error ->
                viewModelScope.launch(Dispatchers.Main) {
                    errorMessage = error ?: "Failed to load stations"
                    isLoading = false
                }
            }
        )
    }

    fun loadCountries() {
        if (countries.isNotEmpty() || isCountriesLoading) return
        
        isCountriesLoading = true
        api.getCountries(
            order = RadioStationOrder.NAME,
            onSuccess = { result ->
                viewModelScope.launch(Dispatchers.Main) {
                    countries = result.sortedBy { it.name }
                    isCountriesLoading = false
                }
            },
            onFail = {
                viewModelScope.launch(Dispatchers.Main) {
                    isCountriesLoading = false
                }
            }
        )
    }

    fun selectCountry(country: RadioCountry) {
        localeRepository.setOverrideCountryCode(country.iso_3166_1)
        currentCountryCode = country.iso_3166_1
        loadStations()
    }

    fun playStation(station: RadioModel) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(station.stationuuid)
            .setUri(station.url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtist(station.name)
                    .setArtworkUri(station.favicon.toUri())
                    .build()
            )
            .apply {
                if (station.url.contains(".m3u8")) {
                    setMimeType(MimeTypes.APPLICATION_M3U8)
                }
            }
            .build()

        isNowPlayingBarVisible = true
        controller?.let { controller ->
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        }
    }

    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        controller?.removeListener(playerListener)
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}
