package praeterii.radio.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import praeterii.radio.data.RadioCountry
import praeterii.radio.data.RadioStation
import praeterii.radio.data.RadioStationClickResult
import praeterii.radio.data.RadioStationOrder
import praeterii.radio.services.RadioStationApiService
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.InetAddress

class RadioStationsRepository(private val userAgent: String = "praeterii.radio") {
    private lateinit var radioBrowserService: RadioStationApiService
    private val initializationMutex = Mutex()

    private suspend fun initialize() {
        if (::radioBrowserService.isInitialized) return

        initializationMutex.withLock {
            // Double-check to prevent re-initialization after acquiring lock
            if (::radioBrowserService.isInitialized) return

            val json = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
            val serverEndpoint = getRadioBrowserServer()
            radioBrowserService = Retrofit.Builder()
                .baseUrl("https://$serverEndpoint")
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(RadioStationApiService::class.java)
        }
    }

    private suspend fun getRadioBrowserServer(): String = withContext(Dispatchers.IO) {
        val servers = InetAddress.getAllByName("all.api.radio-browser.info")
        if (servers.isEmpty()) {
            return@withContext "de1.api.radio-browser.info"
        }
        return@withContext servers.first().canonicalHostName
    }

    suspend fun getCountries(
        order: RadioStationOrder = RadioStationOrder.NAME,
    ): List<RadioCountry> {
        initialize()
        return radioBrowserService.getCountries(
            userAgent = userAgent,
            order = order.value
        )
    }

    suspend fun searchStationsByCountry(
        countryCode: String,
        query: String = "",
        offset: Int = 0,
        limit: Int = 1000,
    ): List<RadioStation> {
        initialize()
        return radioBrowserService.getStations(
            userAgent = userAgent,
            name = query,
            countrycode = countryCode,
            offset = offset,
            limit = limit,
        )
    }

    suspend fun stationClick(
        stationUuid: String,
    ): RadioStationClickResult {
        initialize()
        return radioBrowserService.stationClick(
            userAgent = userAgent,
            stationUuid = stationUuid
        )
    }
}
