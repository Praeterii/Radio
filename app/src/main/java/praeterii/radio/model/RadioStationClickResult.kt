package praeterii.radio.model

import kotlinx.serialization.Serializable

@Serializable
data class RadioStationClickResult(
    val ok: String,
    val message: String,
    val stationuuid: String,
    val name: String,
    val url: String
)
