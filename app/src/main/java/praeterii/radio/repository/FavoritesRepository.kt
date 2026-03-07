package praeterii.radio.repository

import kotlinx.coroutines.flow.Flow
import praeterii.radio.data.local.FavoriteDao
import praeterii.radio.domain.model.RadioModel

class FavoritesRepository(private val favoriteDao: FavoriteDao) {
    val allFavorites: Flow<List<RadioModel>> = favoriteDao.getAllFavorites()

    suspend fun insert(station: RadioModel) {
        favoriteDao.insertFavorite(station)
    }

    suspend fun delete(station: RadioModel) {
        favoriteDao.deleteFavorite(station)
    }

    fun isFavorite(uuid: String): Flow<Boolean> {
        return favoriteDao.isFavorite(uuid)
    }
}
