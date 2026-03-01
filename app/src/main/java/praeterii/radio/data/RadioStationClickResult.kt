package praeterii.radio.data

import kotlinx.serialization.Serializable

@Serializable
data class RadioStationClickResult(
    val ok: Boolean,
    val message: String,
    val stationuuid: String,
    val name: String,
    val url: String
)
