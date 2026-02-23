package praeterii.radio.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
@Stable
@Immutable
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
