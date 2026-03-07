package praeterii.radio.domain.usecase

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import praeterii.radio.data.RadioCountry
import praeterii.radio.data.RadioStationOrder
import praeterii.radio.repository.LocaleRepository
import praeterii.radio.repository.RadioStationsRepository

class GetCountriesUseCase(
    private val repository: RadioStationsRepository,
    private val localeRepository: LocaleRepository
) {
    operator fun invoke(
        order: RadioStationOrder = RadioStationOrder.NAME,
        onSuccess: (List<RadioCountry>) -> Unit,
        onFail: (String?) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val defaultCountryCode = localeRepository.getDefaultCountryCode()
            val comparator = compareByDescending<RadioCountry> { country ->
                country.iso_3166_1.equals(defaultCountryCode, ignoreCase = true)
            }.thenBy { country -> country.name }

            repository.getCountries(order)
                .sortedWith(comparator)
                .let { sortedCountries ->
                    onSuccess(sortedCountries)
                }
        } catch (e: Exception) {
            ensureActive()
            Log.e(GetCountriesUseCase::class.java.name, e.message ?: "Unknown error")
            onFail.invoke(e.message)
        }
    }
}
