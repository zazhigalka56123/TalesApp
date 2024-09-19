package ru.tales.forfamily.domain.api

interface PostBackRepository {
    suspend fun postUserId(userId: String): Boolean
}