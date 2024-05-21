package ru.tales.forfamily.domain.api

import ru.tales.forfamily.domain.Tale

interface ApiRepository {
    suspend fun getTales(): List<Tale>?

    suspend fun postData(login: String): Boolean

    suspend fun getSettings(): Int
}