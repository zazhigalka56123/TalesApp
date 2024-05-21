package ru.tales.forfamily.data.remote

import android.util.Log
import ru.tales.forfamily.domain.Mapper
import ru.tales.forfamily.domain.Tale
import ru.tales.forfamily.domain.UserData
import ru.tales.forfamily.domain.api.ApiRepository
import ru.tales.forfamily.domain.api.SettingsModel

class ApiRepositoryImpl : ApiRepository {
    private val service = ApiBuilder().service()

    override suspend fun getTales(): List<Tale>? {
        return try {
            val response = service.getTales()
            val list = mutableListOf<Tale>()
            var ind = 0
            response.body()?.forEach {
                val x = Mapper().mapApiModelToEntity(it, ind)
                ind += 1
                list.add(x)
            }
            list

        }catch (e: Exception){
            Log.d("response-error", e.message.toString())
            null
        }
    }

    override suspend fun postData(login: String): Boolean{
        return try {
            val request = service.postTales(login = login)
            Log.d("postrequest", request.code().toString())
            Log.d("postrequest", request.raw().toString())
            (request.code() == 200)
        }catch (e: Exception){
            Log.d("POST", e.message.toString())
            false
        }
    }

    override suspend fun getSettings(): Int {
        return try {
            val resp = service.getSettings()
            Log.d("fsd", resp.raw().toString())
            Log.d("fsd", resp.body().toString())
            resp.body()?.get(0)?.counter ?: 20
        }catch (e: Exception){
            Log.d("fsd, e", e.message.toString())
            20
        }


    }

}
