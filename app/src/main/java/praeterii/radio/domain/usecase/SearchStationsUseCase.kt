package praeterii.radio.domain.usecase

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import praeterii.radio.domain.model.RadioModel
import praeterii.radio.repository.RadioStationsRepository

internal class SearchStationsUseCase(
    private val repository: RadioStationsRepository,
    private val favoriteStationIds: StateFlow<Set<String>>,
    private val getFavoritesUseCase: GetFavoritesUseCase,
) {
    operator fun invoke(
        scope: CoroutineScope,
        countryCode: String,
        query: String = "",
        offset: Int = 0,
        limit: Int = 100,
        onSuccess: (List<RadioModel>, Int) -> Unit,
        onFail: (Exception) -> Unit
    ) = scope.launch {
        try {
            var originalCount = 0
            val result = withContext(Dispatchers.IO) {
                val stations = repository.searchStationsByCountry(countryCode, query, offset, limit)
                originalCount = stations.size
                val mappedResults = stations.distinctBy { stationModel ->
                        stationModel.url_resolved
                    }.map { stationModel ->
                        RadioModel(
                            stationuuid = stationModel.stationuuid,
                            name = stationModel.name,
                            url = stationModel.url_resolved,
                            favicon = stationModel.favicon,
                            tags = stationModel.tags.replace(",", " "),
                        )
                    }
                
                if (offset == 0 && query.isEmpty()) {
                    val favorites = getFavoritesUseCase().first()
                    (favorites + mappedResults).distinctBy { it.stationuuid }
                } else {
                    mappedResults.sortedByDescending { radioModel ->
                        favoriteStationIds.value.contains(radioModel.stationuuid)
                    }
                }
            }
            onSuccess(result, originalCount)
        } catch (e: Exception) {
            ensureActive()
            Log.e(SearchStationsUseCase::class.java.name, e.message ?: "Unknown error")
            onFail.invoke(e)
        }
    }
}
