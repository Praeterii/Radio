package praeterii.radio.model

import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class RadioStation(
    val stationuuid: String,
    val name: String,
    val url: String,
    val url_resolved: String,
    val homepage: String,
    val favicon: String,
    val tags: String,
    val country: String,
    val language: String,
)
