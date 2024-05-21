package ru.tales.forfamily.domain.api

import com.google.gson.annotations.SerializedName

data class SettingsModel (
    @SerializedName("count_before_form")
    var counter: Int? = null
)