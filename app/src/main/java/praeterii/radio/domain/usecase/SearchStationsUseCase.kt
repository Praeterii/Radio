package praeterii.radio.domain.usecase

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import praeterii.radio.domain.model.RadioModel
import praeterii.radio.repository.RadioStationsRepository

class SearchStationsUseCase(private val repository: RadioStationsRepository) {
    operator fun invoke(
        countryCode: String,
        query: String = "",
        offset: Int = 0,
        limit: Int = 1000,
        onSuccess: (List<RadioModel>) -> Unit,
        onFail: (String?) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            repository.searchStationsByCountry(countryCode, query, offset, limit)
                .distinctBy { stationModel ->
                    stationModel.url_resolved
                }.map { stationModel ->
                    RadioModel(
                        stationuuid = stationModel.stationuuid,
                        name = stationModel.name,
                        url = stationModel.url_resolved,
                        favicon = stationModel.favicon,
                        tags = stationModel.tags.replace(",", " "),
                    )
                }.let(onSuccess)
        } catch (e: Exception) {
            ensureActive()
            Log.e(SearchStationsUseCase::class.java.name, e.message ?: "Unknown error")
            onFail.invoke(e.message)
        }
    }
}
