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

    @GET("json/stations/bycountrycodeexact/{countryCode}")
    suspend fun getStationsByCountry(
        @Header("User-Agent") userAgent: String,
        @Path("countryCode") countryCode: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 1000,
        @Query("order") order: String = RadioStationOrder.NAME.value,
        @Query("reverse") reverse: Boolean = false
    ): List<RadioStation>

    @GET("json/stations/byname/{search}")
    suspend fun getStationsBySearch(
        @Header("User-Agent") userAgent: String,
        @Path("search") search: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 1000
    ): List<RadioStation>

    @GET("json/url/{stationUuid}")
    suspend fun stationClick(
        @Header("User-Agent") userAgent: String,
        @Path("stationUuid") stationUuid: String
    ): RadioStationClickResult

}