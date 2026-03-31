package praeterii.radio.domain.usecase

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import praeterii.radio.data.RadioCountry
import praeterii.radio.data.RadioStationOrder
import praeterii.radio.repository.LocaleRepository
import praeterii.radio.repository.RadioStationsRepository

class GetCountriesUseCase(
    private val repository: RadioStationsRepository,
    private val localeRepository: LocaleRepository
) {
    operator fun invoke(
        scope: CoroutineScope,
        order: RadioStationOrder = RadioStationOrder.NAME,
        onSuccess: (List<RadioCountry>) -> Unit,
        onFail: (String?) -> Unit
    ) = scope.launch {
        try {
            val sortedCountries = withContext(Dispatchers.IO) {
                val defaultCountryCode = localeRepository.getDefaultCountryCode()
                val comparator = compareByDescending<RadioCountry> { country ->
                    country.iso_3166_1.equals(defaultCountryCode, ignoreCase = true)
                }.thenBy { country -> country.name }

                repository.getCountries(order)
                    .sortedWith(comparator)
            }
            onSuccess(sortedCountries)
        } catch (e: Exception) {
            ensureActive()
            Log.e(GetCountriesUseCase::class.java.name, e.message ?: "Unknown error")
            onFail.invoke(e.message)
        }
    }
}
