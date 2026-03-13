package praeterii.radio.domain.usecase

import kotlinx.coroutines.flow.Flow
import praeterii.radio.domain.model.RadioModel
import praeterii.radio.repository.FavoritesRepository

class GetFavoritesUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(): Flow<List<RadioModel>> = repository.allFavorites
}
