package praeterii.radio.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import praeterii.radio.data.RadioCountry
import praeterii.radio.data.RadioStation
import praeterii.radio.data.RadioStationClickResult
import praeterii.radio.data.RadioStationOrder
import praeterii.radio.services.RadioStationApiService
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class RadioStationsRepository(
    private val context: Context,
    private val userAgent: String = "praeterii.radio"
) {
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

            // Note: You'll need to pass 'context' to this class to access cacheDir
            val cacheSize = 10 * 1024 * 1024L // 10 MiB
            val cache = okhttp3.Cache(context.cacheDir.resolve("http_cache"), cacheSize)

            val okHttpClient = OkHttpClient.Builder()
                .cache(cache) // <--- Add this
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    var request = chain.request()
                    // Optional: Force cache if offline, or set max-age for specific calls
                    request = if (isNetworkAvailable(context))
                        request.newBuilder().header("Cache-Control", "public, max-age=" + 60 * 60 * 24).build()
                    else
                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                    chain.proceed(request)
                }
                .build()

            radioBrowserService = Retrofit.Builder()
                .baseUrl("https://$serverEndpoint")
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(RadioStationApiService::class.java)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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
