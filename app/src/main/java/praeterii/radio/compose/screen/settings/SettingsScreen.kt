package praeterii.radio.compose.screen.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.murgupluoglu.flagkit.FlagKit
import praeterii.radio.compose.country.CountryPickerSheet
import praeterii.radio.data.RadioCountry
import praeterii.radio.theme.RadioTheme
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBarDefaults
import praeterii.radio.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    countries: List<RadioCountry>,
    isCountriesLoading: Boolean,
    currentCountryCode: String,
    stopPlaybackOnTaskRemoved: Boolean,
    onStopPlaybackOnTaskRemovedChange: (Boolean) -> Unit,
    onOpenCountryPicker: () -> Unit,
    onCountrySelect: (RadioCountry) -> Unit,
    onBackClick: () -> Unit
) {
    var showCountryPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                val flagResId = FlagKit.getResId(currentCountryCode)
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_country)) },
                    supportingContent = { Text(currentCountryCode) },
                    trailingContent = {
                        if (flagResId != 0) {
                            Image(
                                painter = painterResource(flagResId),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    modifier = Modifier.clickable {
                        onOpenCountryPicker()
                        showCountryPicker = true
                    }
                )
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_stop_playback_on_app_close)) },
                    supportingContent = { Text(stringResource(R.string.settings_stop_playback_on_app_close_description)) },
                    trailingContent = {
                        Switch(
                            checked = stopPlaybackOnTaskRemoved,
                            onCheckedChange = onStopPlaybackOnTaskRemovedChange
                        )
                    },
                    modifier = Modifier.clickable {
                        onStopPlaybackOnTaskRemovedChange(!stopPlaybackOnTaskRemoved)
                    }
                )
                HorizontalDivider()
            }
        }

        if (showCountryPicker) {
            CountryPickerSheet(
                countries = countries,
                isLoading = isCountriesLoading,
                onCountrySelect = {
                    onCountrySelect(it)
                    showCountryPicker = false
                },
                onDismissRequest = { showCountryPicker = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreview() {
    RadioTheme {
        Surface {
            SettingsScreen(
                countries = listOf(
                    RadioCountry("Poland", "PL", 100),
                    RadioCountry("United Kingdom", "GB", 200),
                    RadioCountry("Germany", "DE", 150)
                ),
                isCountriesLoading = false,
                currentCountryCode = "PL",
                stopPlaybackOnTaskRemoved = true,
                onStopPlaybackOnTaskRemovedChange = {},
                onOpenCountryPicker = {},
                onCountrySelect = {},
                onBackClick = {}
            )
        }
    }
}
