package praeterii.radio.repository

import android.content.Context
import java.util.Locale
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    
    private val _countryCode = MutableStateFlow(getCurrentCountryCode())
    val countryCode = _countryCode.asStateFlow()

    private val _stopPlaybackOnTaskRemoved = MutableStateFlow(getStopPlaybackOnTaskRemoved())
    val stopPlaybackOnTaskRemoved = _stopPlaybackOnTaskRemoved.asStateFlow()

    fun getCurrentCountryCode(): String {
        val savedCountry = prefs.getString(COUNTRY_CODE, null)
        return savedCountry ?: getDefaultCountryCode()
    }

    fun setOverrideCountryCode(countryCode: String?) {
        prefs.edit { putString(COUNTRY_CODE, countryCode) }
        _countryCode.value = countryCode ?: getDefaultCountryCode()
    }

    fun getDefaultCountryCode(): String {
        return Locale.getDefault().country.ifBlank { "US" }
    }

    fun getStopPlaybackOnTaskRemoved(): Boolean {
        return prefs.getBoolean(STOP_PLAYBACK_ON_TASK_REMOVED, true)
    }

    fun setStopPlaybackOnTaskRemoved(stop: Boolean) {
        prefs.edit { putBoolean(STOP_PLAYBACK_ON_TASK_REMOVED, stop) }
        _stopPlaybackOnTaskRemoved.value = stop
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingsRepository? = null

        fun getInstance(context: Context): SettingsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsRepository(context.applicationContext).also { INSTANCE = it }
            }
        }

        private const val COUNTRY_CODE = "country_code"
        private const val STOP_PLAYBACK_ON_TASK_REMOVED = "stop_playback_on_task_removed"
    }
}
