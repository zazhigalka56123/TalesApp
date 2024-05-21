package ru.tales.forfamily.domain.player

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import ru.tales.forfamily.R
import ru.tales.forfamily.domain.player.MyPlayerNotificationManager.Companion.NOTIFICATION_LARGE_ICON_SIZE
import kotlinx.coroutines.*
import java.lang.Exception

class PlayerDescriptionAdapter(
    private val controller: MediaControllerCompat,
    private val context: Context
) : PlayerNotificationManager.MediaDescriptionAdapter {

    var currentIconUri: Uri? = null
    var currentBitmap: Bitmap? = null

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val glideOptions = RequestOptions()
        .fallback(R.drawable.ic_launcher_background)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
    
    override fun createCurrentContentIntent(player: Player): PendingIntent? =
        controller.sessionActivity

    override fun getCurrentContentText(player: Player) =
        try {
            player.mediaMetadata.artist.toString()
        } catch (e: Exception) {
            ""
        }

    override fun getCurrentContentTitle(player: Player) =
        try {
            player.mediaMetadata.title.toString()
        } catch (e: Exception) {
            ""
        }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        try {
            val iconUri = player.mediaMetadata.artworkUri
            return if (currentIconUri != iconUri || currentBitmap == null) {
                currentIconUri = iconUri
                serviceScope.launch {
                    currentBitmap = iconUri?.let {
                        resolveUriAsBitmap(it)
                    }
                    currentBitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                currentBitmap
            }
        } catch (e: Exception) {
            return null
        }
    }

    private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            Glide.with(context).applyDefaultRequestOptions(glideOptions)
                .asBitmap()
                .load(uri)
                .submit(
                    NOTIFICATION_LARGE_ICON_SIZE,
                    NOTIFICATION_LARGE_ICON_SIZE
                )
                .get()
        }
    }
}