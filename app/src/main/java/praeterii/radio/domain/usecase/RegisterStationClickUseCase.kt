package praeterii.radio.domain.usecase

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import praeterii.radio.data.RadioStationClickResult
import praeterii.radio.repository.RadioStationsRepository

class RegisterStationClickUseCase(private val repository: RadioStationsRepository) {
    operator fun invoke(
        stationUuid: String,
        onSuccess: (RadioStationClickResult) -> Unit,
        onFail: (String?) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            repository.stationClick(stationUuid).let(onSuccess)
        } catch (e: Exception) {
            ensureActive()
            Log.e(RegisterStationClickUseCase::class.java.name, e.message ?: "Unknown error")
            onFail.invoke(e.message)
        }
    }
}
