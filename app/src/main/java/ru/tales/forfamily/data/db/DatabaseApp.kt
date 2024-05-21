package ru.tales.forfamily.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.tales.forfamily.domain.db.TaleDbModel

@Database(
    entities = [TaleDbModel::class],
    version = 1,
    exportSchema = true,
)
abstract class DatabaseApp : RoomDatabase() {

    abstract fun taleDao(): TaleDao

    companion object {
        private var INSTANCE: DatabaseApp? = null
        private val LOCK = Any()
        private const val DB_NAME = "TALES_0"

        fun getInstance(application: Application): DatabaseApp {
            INSTANCE?.let {
                return it
            }

            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }

                val db = Room.databaseBuilder(
                    application,
                    DatabaseApp::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = db

                return db
            }
        }
    }
}