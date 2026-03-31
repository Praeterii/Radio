package praeterii.radio.domain.usecase

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import praeterii.radio.domain.model.RadioModel
import praeterii.radio.repository.RadioStationsRepository

class SearchStationsUseCase(
    private val repository: RadioStationsRepository,
    private val favoriteStationIds: StateFlow<Set<String>>,
) {
    operator fun invoke(
        scope: CoroutineScope,
        countryCode: String,
        query: String = "",
        offset: Int = 0,
        limit: Int = 1000,
        onSuccess: (List<RadioModel>) -> Unit,
        onFail: (Exception) -> Unit
    ) = scope.launch {
        try {
            val result = withContext(Dispatchers.IO) {
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
                    }.sortedByDescending { radioModel ->
                        favoriteStationIds.value.contains(radioModel.stationuuid)
                    }
            }
            onSuccess(result)
        } catch (e: Exception) {
            ensureActive()
            Log.e(SearchStationsUseCase::class.java.name, e.message ?: "Unknown error")
            onFail.invoke(e)
        }
    }
}
