package praeterii.radio.playback

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class PlaybackManager(context: Context) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    var currentMediaItem by mutableStateOf<MediaItem?>(null)
        private set
    var isPlaying by mutableStateOf(false)
        private set
    var currentMetadata by mutableStateOf<androidx.media3.common.MediaMetadata?>(null)
        private set

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            currentMediaItem = mediaItem
            currentMetadata = mediaItem?.mediaMetadata
        }

        override fun onIsPlayingChanged(playing: Boolean) {
            isPlaying = playing
        }

        override fun onMediaMetadataChanged(mediaMetadata: androidx.media3.common.MediaMetadata) {
            currentMetadata = mediaMetadata
        }
    }

    init {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            controller?.let { controller ->
                controller.addListener(playerListener)
                currentMediaItem = controller.currentMediaItem
                currentMetadata = controller.currentMediaItem?.mediaMetadata
                isPlaying = controller.isPlaying
            }
        }, MoreExecutors.directExecutor())
    }

    fun play(mediaItem: MediaItem) {
        controller?.let { controller ->
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        }
    }

    fun togglePlayPause() {
        controller?.let { controller ->
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }
        }
    }

    fun release() {
        controller?.removeListener(playerListener)
        controllerFuture?.let { controllerFuture ->
            MediaController.releaseFuture(controllerFuture)
        }
    }
}
