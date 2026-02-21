package praeterii.radio

import android.app.Application
import android.content.ComponentName
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.r.cohen.radiobrowserandroid.RadioBrowserApi
import com.r.cohen.radiobrowserandroid.models.RadioBrowserOrder
import com.r.cohen.radiobrowserandroid.models.RadioBrowserStation
import praeterii.radio.playback.PlaybackService
import praeterii.radio.repository.LocaleRepository

class RadioViewModel(application: Application) : AndroidViewModel(application) {
    private val api by lazy { RadioBrowserApi() }
    val localeRepository by lazy { LocaleRepository(application) }
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    var stations by mutableStateOf<List<RadioBrowserStation>>(emptyList())
    var currentCountryCode by mutableStateOf(localeRepository.getCurrentCountryCode())
        private set

    init {
        val sessionToken =
            SessionToken(application, ComponentName(application, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(application, sessionToken).buildAsync()
    }

    fun loadStations() {
        api.getStationsByCountry(
            countryCode = currentCountryCode,
            limit = 1000,
            order = RadioBrowserOrder.BY_CLICKCOUNT,
            onSuccess = { result ->
                stations = result.filter { station -> station.url.contains("https://") }
            },
            onFail = { /* Handle error */ }
        )
    }

    fun toggleLocale() {
        val newCode = if (currentCountryCode == "PL") "US" else "PL"
        localeRepository.setOverrideCountryCode(newCode)
        currentCountryCode = newCode
        loadStations()
    }

    fun playStation(station: RadioBrowserStation) {
        val mediaItem = MediaItem.Builder()
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

        controller?.let { controller ->
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        }
    }

    override fun onCleared() {
        super.onCleared()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}
