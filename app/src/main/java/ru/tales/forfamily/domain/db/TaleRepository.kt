package ru.tales.forfamily.domain.db

import ru.tales.forfamily.domain.Tale

interface TaleRepository {

    suspend fun addTale(tale: Tale)

    suspend fun getTale(taleId: String): Tale

    suspend fun getTales(): List<Tale>


    suspend fun clearPlaylists()
}