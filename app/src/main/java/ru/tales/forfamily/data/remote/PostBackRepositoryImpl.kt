package ru.tales.forfamily.data.remote

import android.util.Log
import ru.tales.forfamily.domain.api.PostBackRepository

class PostBackRepositoryImpl : PostBackRepository {
    private val service = ApiBuilder().postBackService()

    override suspend fun postUserId(userId: String): Boolean {
        return try {
            val request = service.postUserId(userId = userId)
            Log.d("postrequest", request.code().toString())
            Log.d("postrequest", request.raw().toString())
            (request.code() == 200)
        }catch (e: Exception){
            Log.d("POST", e.message.toString())
            false
        }
    }


}