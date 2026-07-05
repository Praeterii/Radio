package praeterii.radio.playback

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import praeterii.radio.MainActivity
import praeterii.radio.R
import praeterii.radio.repository.SettingsRepository

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private val settingsRepository by lazy { SettingsRepository.getInstance(this) }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .build()

        // Use ForwardingPlayer to provide a fallback title and handle live sync on resume
        val forwardingPlayer = object : ForwardingPlayer(player) {
            override fun play() {
                if (isCurrentMediaItemLive) {
                    seekToDefaultPosition()
                }
                super.play()
            }

            override fun getMediaMetadata(): MediaMetadata {
                val metadata = super.getMediaMetadata()
                if (metadata.title.isNullOrEmpty()) {
                    val fallbackTitle = if (metadata.artist.isNullOrEmpty()) {
                        getString(R.string.app_name)
                    } else {
                        metadata.artist
                    }
                    return metadata.buildUpon()
                        .setTitle(fallbackTitle)
                        .build()
                }
                return metadata
            }
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, forwardingPlayer)
            .setSessionActivity(pendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player ?: return
        val stopPlaybackOnTaskRemoved = settingsRepository.getStopPlaybackOnTaskRemoved()
        
        // If the radio is paused, has no content, or the user wants to stop on app close, stop the service
        if (!player.playWhenReady || player.mediaItemCount == 0 || stopPlaybackOnTaskRemoved) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
