package praeterii.radio.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Stable
@Immutable
@Serializable
@Entity(tableName = "favorites")
data class RadioModel(
    @PrimaryKey
    val stationuuid: String,
    val name: String,
    val url: String,
    val favicon: String,
    val tags: String = ""
) {
    fun toMediaItem() : MediaItem = MediaItem.Builder()
        .setMediaId(stationuuid)
        .setUri(url)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setArtist(name)
                .setArtworkUri(favicon.toUri())
                .build()
        )
        .apply {
            if (url.contains(".m3u8")) {
                setMimeType(MimeTypes.APPLICATION_M3U8)
            }
        }
        .build()
}