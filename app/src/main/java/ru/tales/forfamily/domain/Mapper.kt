package ru.tales.forfamily.domain

import ru.tales.forfamily.domain.api.ApiTaleModel
import ru.tales.forfamily.domain.db.TaleDbModel

class Mapper {

    fun mapApiModelToEntity(m: ApiTaleModel, ind: Int) = Tale(
        id = m.id.toString(),
        index = ind,
        name = m.name.toString(),
        img = m.imgUri.toString(),
        uri = m.uriAudio.toString(),
        duration = "",
        text = m.text.toString(),
        isSaved = false
        )

    fun mapEntityToDbModel(tale: Tale) = TaleDbModel(
        id = tale.id,
        index = tale.index,
        name = tale.name,
        img = tale.img,
        uri = tale.uri,
        duration = tale.duration,
        text = tale.text,
        isSaved = true
    )
    fun mapDbModelToEntity(it: TaleDbModel) =
        Tale(
            id = it.id,
            index = it.index,
            name = it.name,
            img = it.img,
            uri = it.uri,
            duration = it.duration,
            text = it.text,
            isSaved = true
        )

    fun mapDbModelsToEntities(list: List<TaleDbModel>) = list.map {
        Tale(
            id = it.id,
            index = it.index,
            name = it.name,
            img = it.img,
            uri = it.uri,
            duration = it.duration,
            text = it.text,
            isSaved = true
        )
    }
}