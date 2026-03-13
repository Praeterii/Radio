package praeterii.radio

import android.app.Application
import android.util.Log
import androidx.annotation.Keep
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import praeterii.radio.playback.PlaybackManager
import praeterii.radio.repository.FavoritesRepository
import praeterii.radio.repository.LocaleRepository

@Keep
class RadioViewModel(application: Application) : AndroidViewModel(application) {
    private val api by lazy { RadioStationsRepository() }
    private val localeRepository by lazy { LocaleRepository(application) }
    private val favoritesRepository by lazy {
        FavoritesRepository(RadioDatabase.getDatabase(application).favoriteDao())
    }
    
    private val playbackManager = PlaybackManager(application)

    var stations by mutableStateOf<List<RadioModel>>(emptyList())
    var currentCountryCode by mutableStateOf(localeRepository.getCurrentCountryCode())
        private set

    var countries by mutableStateOf<List<RadioCountry>>(emptyList())
        private set
    var isCountriesLoading by mutableStateOf(false)
        private set

    val currentMediaItem get() = playbackManager.currentMediaItem
    val isPlaying get() = playbackManager.isPlaying

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

    val currentlyPlayingId: String?
        get() = currentMediaItem?.mediaId

    private var searchJob: Job? = null

    fun loadStations(query: String = searchQuery) {
        errorMessage = null
        isLoading = true
        SearchStationsUseCase(repository = api, favoriteStationIds = favoriteStationIds)(
            countryCode = currentCountryCode,
            query = query,
            limit = 1000,
            onSuccess = { result ->
                stations = result
                isLoading = false
            },
            onFail = { error ->
                errorMessage = error ?: "Failed to load stations"
                isLoading = false
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
        playbackManager.play(station.toMediaItem())

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
        }
    }

    fun togglePlayPause() {
        playbackManager.togglePlayPause()
    }

    override fun onCleared() {
        super.onCleared()
        playbackManager.release()
    }
}
