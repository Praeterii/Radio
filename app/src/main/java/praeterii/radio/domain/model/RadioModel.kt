package praeterii.radio.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
@Immutable
data class RadioModel(
    val stationuuid: String,
    val name: String,
    val url: String,
    val favicon: String,
)