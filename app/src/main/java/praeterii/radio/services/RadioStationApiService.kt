package praeterii.radio.services

import praeterii.radio.data.RadioStationClickResult
import praeterii.radio.data.RadioCountry
import praeterii.radio.data.RadioStationOrder
import praeterii.radio.data.RadioStation
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RadioStationApiService {
    @GET("json/countries")
    suspend fun getCountries(
        @Header("User-Agent") userAgent: String,
        @Query("order") order: String = RadioStationOrder.NAME.value
    ): List<RadioCountry>

    @GET("json/stations/search")
    suspend fun getStations(
        @Header("User-Agent") userAgent: String,
        @Query("name") name: String = "",
        @Query("countrycode") countrycode: String?,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 1000,
        @Query("order") order: String = RadioStationOrder.CLICKCOUNT.value,
        @Query("reverse") reverse: Boolean = true,
        @Query("is_https") isHttps: Boolean = true,
        @Query("hidebroken") hideBroken: Boolean = true,
    ): List<RadioStation>

    @GET("json/url/{stationuuid}")
    suspend fun stationClick(
        @Header("User-Agent") userAgent: String,
        @Path("stationuuid") stationUuid: String
    ): RadioStationClickResult

}