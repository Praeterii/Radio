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
import praeterii.radio.repository.RadioBrowserApi
import praeterii.radio.model.RadioStationOrder
import praeterii.radio.model.RadioStation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import praeterii.radio.playback.PlaybackService
import praeterii.radio.repository.LocaleRepository

@Keep
@Suppress("Unused")
class RadioViewModel(application: Application) : AndroidViewModel(application) {
    private val api by lazy { RadioBrowserApi() }
    val localeRepository by lazy { LocaleRepository(application) }
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    var stations by mutableStateOf<List<RadioStation>>(emptyList())
    var currentCountryCode by mutableStateOf(localeRepository.getCurrentCountryCode())
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
                viewModelScope.launch(Dispatchers.Default) {
                    val filtered = result.filter { station -> station.url.contains("https://") }
                    withContext(Dispatchers.Main) {
                        stations = filtered
                        isLoading = false
                    }
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

    fun toggleLocale() {
        // TODO replace with current locale and secondary locale
        val newCode = if (currentCountryCode == "PL") "US" else "PL"
        localeRepository.setOverrideCountryCode(newCode)
        currentCountryCode = newCode
        loadStations()
    }

    fun playStation(station: RadioStation) {
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
