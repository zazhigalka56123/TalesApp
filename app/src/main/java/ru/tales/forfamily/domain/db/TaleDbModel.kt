package ru.tales.forfamily.domain.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tales")
data class TaleDbModel(
    @PrimaryKey
    var id: String,
    var index: Int,
    var name: String,
    var img: String,
    var uri: String,
    var duration: String,
    var text: String,
    var isSaved: Boolean
)
