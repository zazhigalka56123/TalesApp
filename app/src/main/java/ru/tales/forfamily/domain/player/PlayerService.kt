package ru.tales.forfamily.domain.player

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.ServiceCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes

class PlayerService : MediaBrowserServiceCompat() {
    private lateinit var notificationManager: MyPlayerNotificationManager
    private lateinit var currentPlayer: Player

    private lateinit var mediaSession: MediaSessionCompat

    companion object {
        var isForegroundService = true
        const val NOW_PLAYING_NOTIFICATION_ID = 312312
        const val NOW_PLAYING_CHANNEL_ID = "ru.tales.forfamily.safsdfdsfdsfsd"
        var exoPlayer: ExoPlayer? = null
    }

    private var playerListener = playerListener()
    private var notificationListener =
        object : com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener {
            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {
                Log.d("fssd", "posted")
                if (ongoing) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        startForeground(notificationId, notification)
                    } else {
                        startForeground(notificationId, notification,
                            FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
                    }

                    startForeground(notificationId, notification)
                } else {
                    ServiceCompat.stopForeground(
                        this@PlayerService,
                        ServiceCompat.STOP_FOREGROUND_REMOVE
                    )
                }
            }
        }

    private val attributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    override fun onCreate() {
        super.onCreate()
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, PendingIntent.FLAG_IMMUTABLE)
            }
        mediaSession = MediaSessionCompat(this, "MusicService")
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }

        notificationManager = MyPlayerNotificationManager(
            this,
            mediaSession.sessionToken,
            notificationListener
        )

        exoPlayer = ExoPlayer
            .Builder(this)
            .setAudioAttributes(attributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setPauseAtEndOfMediaItems(false)
            .build()

        exoPlayer!!.addListener(playerListener)
        exoPlayer!!.playWhenReady = true

        currentPlayer = exoPlayer!!

        sessionToken = mediaSession.sessionToken

        notificationManager.showNotificationForPlayer(currentPlayer)
    }

    private fun playerListener() = object : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Log.e("gtjkrtr", playbackState.toString())
            Log.e("gtjkrtr", Player.STATE_READY.toString())
            Log.e("gtjkrtr", Player.STATE_BUFFERING.toString())
            Log.e("gtjkrtr", Player.STATE_IDLE.toString())
            Log.e("gtjkrtr", Player.STATE_ENDED.toString())
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(exoPlayer!!)
                    if (playbackState == Player.STATE_READY) {
                        if (!playWhenReady) {
                        }
                    }
                }
                else -> {
                    notificationManager.hideNotification()
                }
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        currentPlayer.stop()
    }

    override fun onDestroy() {
        mediaSession.run {
            isActive = false
            release()
        }

        exoPlayer!!.removeListener(playerListener)
        exoPlayer!!.release()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? = null

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
    }
}