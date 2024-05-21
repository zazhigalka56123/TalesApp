package ru.tales.forfamily.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.tales.forfamily.domain.api.ApiTaleModel
import ru.tales.forfamily.domain.api.SettingsModel


interface ApiService {
    @GET("/index.php")
    suspend fun getTales(): Response<List<ApiTaleModel>>

    @GET("/post.php")
    suspend fun postTales(@Query("login") login: String): Response<Unit>

    @GET("/settings.php")
    suspend fun getSettings(): Response<List<SettingsModel>?>
}