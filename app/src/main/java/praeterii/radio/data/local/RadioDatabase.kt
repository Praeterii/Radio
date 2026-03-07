package praeterii.radio.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import praeterii.radio.domain.model.RadioModel

@Database(entities = [RadioModel::class], version = 1, exportSchema = false)
abstract class RadioDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: RadioDatabase? = null

        fun getDatabase(context: Context): RadioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RadioDatabase::class.java,
                    "radio_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
