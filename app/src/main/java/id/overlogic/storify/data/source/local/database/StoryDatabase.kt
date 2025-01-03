package id.overlogic.storify.data.source.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.overlogic.storify.data.source.local.dao.RemoteKeysDao
import id.overlogic.storify.data.source.local.dao.StoryDao
import id.overlogic.storify.data.source.local.entity.StoryEntity

@Database(entities = [StoryEntity::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, StoryDatabase::class.java, "story"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
