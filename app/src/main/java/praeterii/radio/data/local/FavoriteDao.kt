package praeterii.radio.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import praeterii.radio.domain.model.RadioModel

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<RadioModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(station: RadioModel)

    @Delete
    suspend fun deleteFavorite(station: RadioModel)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE stationuuid = :uuid)")
    fun isFavorite(uuid: String): Flow<Boolean>
}
