package ru.tales.forfamily.domain.api

import com.google.gson.annotations.SerializedName

data class ApiTaleModel(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("img_index")
    var imgUri: String? = null,

    @SerializedName("about")
    var text: String? = null,

    @SerializedName("audio")
    var uriAudio: String? = null,
)