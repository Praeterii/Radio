@file:Suppress("unused")

package praeterii.radio

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.annotation.Keep
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import praeterii.radio.repository.RadioStationsRepository
import praeterii.radio.data.RadioStationOrder
import praeterii.radio.data.RadioCountry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import praeterii.radio.data.local.RadioDatabase
import praeterii.radio.domain.model.RadioModel
import praeterii.radio.domain.usecase.GetCountriesUseCase
import praeterii.radio.domain.usecase.RegisterStationClickUseCase
import praeterii.radio.domain.usecase.SearchStationsUseCase
import praeterii.radio.playback.PlaybackService
import praeterii.radio.repository.FavoritesRepository
import praeterii.radio.repository.LocaleRepository

@Keep
@Suppress("Unused")
class RadioViewModel(application: Application) : AndroidViewModel(application) {
    private val api by lazy { RadioStationsRepository() }
    private val localeRepository by lazy { LocaleRepository(application) }
    private val favoritesRepository by lazy {
        FavoritesRepository(RadioDatabase.getDatabase(application).favoriteDao())
    }
    
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

    var searchQuery by mutableStateOf("")
        private set

    val favoriteStationIds: StateFlow<Set<String>> = favoritesRepository.allFavorites
        .map { favorites -> favorites.map { it.stationuuid }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private var searchJob: Job? = null

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

    fun loadStations(query: String = searchQuery) {
        errorMessage = null
        isLoading = true
        SearchStationsUseCase(repository = api)(
            countryCode = currentCountryCode,
            query = query,
            limit = 1000,
            onSuccess = { result ->
                viewModelScope.launch(Dispatchers.Main) {
                    stations = result.sortedByDescending { favoriteStationIds.value.contains(it.stationuuid) }
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

    fun onSearchQueryChange(query: String) {
        if (query == searchQuery) return
        searchQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(333)
            loadStations(query)
        }
    }

    fun loadCountries() {
        if (countries.isNotEmpty() || isCountriesLoading) return
        
        isCountriesLoading = true
        GetCountriesUseCase(repository = api, localeRepository = localeRepository)(
            order = RadioStationOrder.NAME,
            onSuccess = { result ->
                countries = result
                isCountriesLoading = false
            },
            onFail = {
                isCountriesLoading = false
            }
        )
    }

    fun selectCountry(country: RadioCountry) {
        localeRepository.setOverrideCountryCode(country.iso_3166_1)
        currentCountryCode = country.iso_3166_1
        searchQuery = ""
        loadStations()
    }

    fun playStation(station: RadioModel) {
        isNowPlayingBarVisible = true
        controller?.let { controller ->
            controller.setMediaItem(station.toMediaItem())
            controller.prepare()
            controller.play()
        }

        RegisterStationClickUseCase(repository = api)(
            stationUuid = station.stationuuid,
            onSuccess = { result ->
                Log.d("RadioViewModel", "Station click registered: ${result.ok}")
            },
            onFail = { error ->
                Log.e("RadioViewModel", "Failed to register station click: $error")
            }
        )
    }

    fun toggleFavorite(station: RadioModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFav = favoriteStationIds.value.contains(station.stationuuid)
            if (isFav) {
                favoritesRepository.delete(station)
            } else {
                favoritesRepository.insert(station)
            }
            // Re-sort the current list to keep favorites at the top
            viewModelScope.launch(Dispatchers.Main) {
                stations = stations.sortedByDescending { 
                    if (it.stationuuid == station.stationuuid) !isFav else favoriteStationIds.value.contains(it.stationuuid)
                }
            }
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
