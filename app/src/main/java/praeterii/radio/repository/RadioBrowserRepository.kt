package praeterii.radio.repository

import android.util.Log
import praeterii.radio.data.RadioStationClickResult
import praeterii.radio.data.RadioCountry
import praeterii.radio.data.RadioStationOrder
import praeterii.radio.data.RadioStation
import praeterii.radio.services.RadioStationApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import praeterii.radio.domain.model.RadioModel
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RadioStationsRepository(private val userAgent: String = "praeterii.radio") {
    private val radioBrowserService: RadioStationApiService by lazy {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        Retrofit.Builder()
            .baseUrl("https://all.api.radio-browser.info")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RadioStationApiService::class.java)
    }

    fun getCountries(
        order: RadioStationOrder = RadioStationOrder.NAME,
        onSuccess: (List<RadioCountry>) -> Unit,
        onFail: (String?) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            radioBrowserService.getCountries(
                userAgent = userAgent,
                order = order.value
            ).let(onSuccess)
        } catch (e: Exception) {
            ensureActive()
            handleApiException(e, onFail)
        }
    }

    fun getStationsByCountry(
        countryCode: String,
        offset: Int = 0,
        limit: Int = 1000,
        order: RadioStationOrder = RadioStationOrder.NAME,
        reverse: Boolean = false,
        onSuccess: (List<RadioModel>) -> Unit,
        onFail: (String?) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            radioBrowserService.getStationsByCountry(
                userAgent = userAgent,
                countryCode = countryCode,
                offset = offset,
                limit = limit,
                order = order.value,
                reverse = reverse
            ).filter { station ->
                station.url.contains("https://")
            }.map { stationModel ->
                RadioModel(
                    stationuuid = stationModel.stationuuid,
                    name = stationModel.name,
                    url = stationModel.url,
                    favicon = stationModel.favicon
                )
            }.let(onSuccess)
        } catch (e: Exception) {
            ensureActive()
            handleApiException(e, onFail)
        }
    }

    fun searchStationsByName(
        search: String,
        offset: Int = 0,
        limit: Int = 1000,
        onSuccess: (List<RadioStation>) -> Unit,
        onFail: (String?) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        if (search.isEmpty()) {
            onFail.invoke("search cannot be empty")
            return@launch
        }
        try {
            radioBrowserService.getStationsBySearch(
                userAgent = userAgent,
                search = search,
                offset = offset,
                limit = limit
            ).let(onSuccess)
        } catch (e: Exception) {
            ensureActive()
            handleApiException(e, onFail)
        }
    }

    fun stationClick(
        stationUuid: String,
        onSuccess: (RadioStationClickResult) -> Unit,
        onFail: (String?) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            radioBrowserService.stationClick(
                userAgent = userAgent,
                stationUuid = stationUuid
            ).let(onSuccess)
        } catch (e: Exception) {
            ensureActive()
            handleApiException(e, onFail)
        }
    }

    private fun handleApiException(exception: Exception, onFail: (String?) -> Unit) {
        Log.e(RadioStationsRepository::class.java.name, exception.message ?: "Unknown error")
        onFail.invoke(exception.message)
    }
}
