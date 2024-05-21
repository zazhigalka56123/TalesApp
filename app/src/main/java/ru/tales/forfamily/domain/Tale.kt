package ru.tales.forfamily.domain

import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata

data class Tale(
    var id: String,
    var index: Int,
    var name: String,
    var img: String,
    var uri: String,
    var duration: String,
    var text: String,
    var isSaved: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toBoolean()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(index)
        parcel.writeString(name)
        parcel.writeString(img)
        parcel.writeString(uri)
        parcel.writeString(duration)
        parcel.writeString(text)
        parcel.writeString(isSaved.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tale> {
        override fun createFromParcel(parcel: Parcel): Tale {
            return Tale(parcel)
        }

        override fun newArray(size: Int): Array<Tale?> {
            return arrayOfNulls(size)
        }
    }

    fun toMediaItem(): MediaItem {
        val item = MediaItem.fromUri(uri)
        val b = Bundle()
        b.putInt("index", index)
        val data = MediaMetadata.Builder()
            .setTitle(name)
            .setArtist("")
            .setAlbumTitle(name)
            .setExtras(b)
            .setArtworkUri(Uri.parse(img))
            .build()

        return item.buildUpon().setMediaMetadata(data).setMediaId(id.toString()).build()
    }

}
