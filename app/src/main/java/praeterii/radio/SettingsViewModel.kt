package praeterii.radio

import android.app.Application
import androidx.annotation.Keep
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import praeterii.radio.data.RadioCountry
import praeterii.radio.data.RadioStationOrder
import praeterii.radio.domain.usecase.GetCountriesUseCase
import praeterii.radio.repository.LocaleRepository
import praeterii.radio.repository.RadioStationsRepository

@Keep
internal class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val api by lazy { RadioStationsRepository(application) }
    private val localeRepository by lazy { LocaleRepository.getInstance(application) }

    var countries by mutableStateOf<List<RadioCountry>>(emptyList())
        private set
    var isCountriesLoading by mutableStateOf(false)
        private set
    var currentCountryCode by mutableStateOf(localeRepository.getCurrentCountryCode())
        private set

    init {
        viewModelScope.launch {
            localeRepository.countryCode.collect {
                currentCountryCode = it
            }
        }
    }

    fun loadCountries() {
        if (countries.isNotEmpty() || isCountriesLoading) return

        isCountriesLoading = true
        GetCountriesUseCase(repository = api, localeRepository = localeRepository)(
            scope = viewModelScope,
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
    }
}
