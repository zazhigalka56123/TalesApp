package ru.tales.forfamily.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PostBackService {
    @GET("/postback")
    suspend fun postUserId(@Query("cid") userId: String): Response<Unit>
}