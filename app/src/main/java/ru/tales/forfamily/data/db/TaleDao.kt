package ru.tales.forfamily.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.tales.forfamily.domain.db.TaleDbModel

@Dao
interface TaleDao {

    @Query("SELECT * FROM tales")
    suspend fun getTales(): List<TaleDbModel>

    @Query("SELECT * FROM tales WHERE id=:taleId LIMIT 1")
    suspend fun getTale(taleId: String) : TaleDbModel


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTale(taleDbModel: TaleDbModel)


    @Query("DELETE FROM tales")
    suspend fun clearTales()
}