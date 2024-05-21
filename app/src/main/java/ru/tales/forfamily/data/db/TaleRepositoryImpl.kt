package ru.tales.forfamily.data.db

import android.app.Application
import ru.tales.forfamily.domain.Mapper
import ru.tales.forfamily.domain.Tale
import ru.tales.forfamily.domain.db.TaleRepository

class TaleRepositoryImpl(application: Application): TaleRepository {

    private val taleDao = DatabaseApp.getInstance(application).taleDao()
    private val mapper = Mapper()

    override suspend fun addTale(tale: Tale) =
        taleDao.addTale(mapper.mapEntityToDbModel(tale))

    override suspend fun getTale(taleId: String) =
        mapper.mapDbModelToEntity(taleDao.getTale(taleId))

    override suspend fun getTales(): List<Tale>{
        return mapper.mapDbModelsToEntities(taleDao.getTales())
    }
    override suspend fun clearPlaylists() {
        taleDao.clearTales()
    }
}