package praeterii.radio.model

import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class RadioCountry(
    val name: String,
    val iso_3166_1: String,
    val stationcount: Int
)
