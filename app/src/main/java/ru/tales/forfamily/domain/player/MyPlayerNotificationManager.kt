package ru.tales.forfamily.domain.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import ru.tales.forfamily.R
import ru.tales.forfamily.domain.player.PlayerService.Companion.NOW_PLAYING_CHANNEL_ID
import ru.tales.forfamily.domain.player.PlayerService.Companion.NOW_PLAYING_NOTIFICATION_ID


class MyPlayerNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    private val listener: PlayerNotificationManager.NotificationListener
) {
    private val manager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)

        val name: CharSequence = context.getString(R.string.app_name)
        val description = "description"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(NOW_PLAYING_CHANNEL_ID, name, importance)
        channel.description = description
        val notificationManager: NotificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)


        val builder = PlayerNotificationManager.Builder(
            context,
            NOW_PLAYING_NOTIFICATION_ID,
            NOW_PLAYING_CHANNEL_ID
        )

        with(builder) {
            setMediaDescriptionAdapter(PlayerDescriptionAdapter(mediaController, context))
        }

        builder.setNotificationListener(listener)
        manager = builder.build()
        manager.setMediaSessionToken(sessionToken)
        manager.setSmallIcon(R.drawable.ic_launcher_background)
        manager.setPriority(NotificationCompat.PRIORITY_LOW);
        manager.setUseRewindAction(false)
        manager.setUseFastForwardAction(false)
    }

    fun hideNotification() {
        manager.setPlayer(null)
    }

    fun showNotificationForPlayer(player: Player) {
        Log.d("adfsda", "show")
        manager.setPlayer(player)
    }

    companion object {
        const val NOTIFICATION_LARGE_ICON_SIZE = 300
    }
}