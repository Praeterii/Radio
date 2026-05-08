package praeterii.radio.repository

import android.content.Context
import java.util.Locale
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocaleRepository private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE)
    private val _countryCode = MutableStateFlow(getCurrentCountryCode())
    val countryCode = _countryCode.asStateFlow()

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

    companion object {
        @Volatile
        private var INSTANCE: LocaleRepository? = null

        fun getInstance(context: Context): LocaleRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocaleRepository(context.applicationContext).also { INSTANCE = it }
            }
        }

        const val COUNTRY_CODE = "country_code"
    }
}
