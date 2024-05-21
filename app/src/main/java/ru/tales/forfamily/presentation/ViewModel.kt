package ru.tales.forfamily.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import ru.tales.forfamily.domain.Tale

class ViewModel(application: Application) : AndroidViewModel(application) {

    var isPlayling = MutableLiveData(false)
    var stop = true
    var backPay = false
    var mode: MutableLiveData<Mode> = MutableLiveData(Mode.DEFAULT_MODE)

    var onMediaMetadataChanged: ((MediaMetadata) -> Unit)? = null
    var onIsPlayingChanged: ((Boolean) -> Unit)? = null
    var onUserAlertCallback: ((String) -> Unit)? = null
    var onTracksChanged: (() -> Unit)? = null
    var showPopUp: (() -> Unit)? = null
    val list: MutableLiveData<List<Tale>> = MutableLiveData(listOf())


    var tales: Array<Tale> = emptyArray()

    val playerListener = object : Player.Listener {

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            onMediaMetadataChanged?.invoke(mediaMetadata)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            onIsPlayingChanged?.invoke(isPlaying)
        }

        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
//            onTracksChanged?.invoke()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            onTracksChanged?.invoke()
        }
    }

}