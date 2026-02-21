package praeterii.radio.repository

import android.content.Context
import java.util.Locale
import androidx.core.content.edit

class LocaleRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE)

    fun getCurrentCountryCode(): String {
        val savedCountry = prefs.getString(COUNTRY_CODE, null)
        return savedCountry ?: Locale.getDefault().country.ifBlank { "US" }
    }

    fun setOverrideCountryCode(countryCode: String?) {
        prefs.edit { putString(COUNTRY_CODE, countryCode) }
    }

    private companion object {
        const val COUNTRY_CODE = "country_code"
    }
}
