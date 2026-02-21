package praeterii.radio.compose.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.murgupluoglu.flagkit.FlagKit
import com.r.cohen.radiobrowserandroid.models.RadioBrowserStation
import praeterii.radio.R
import praeterii.radio.compose.station.StationItem
import praeterii.radio.ui.theme.RadioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RadioScreen(
    stations: List<RadioBrowserStation>,
    currentCountryCode: String,
    onStationClick: (RadioBrowserStation) -> Unit,
    onToggleLocale: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                actions = {
                    val flagResId = FlagKit.getResId(currentCountryCode)
                    if (flagResId != 0) {
                        IconButton(onClick = onToggleLocale) {
                            Image(
                                painter = painterResource(flagResId),
                                contentDescription = "Toggle Locale",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            items(stations) { station ->
                StationItem(
                    station = station,
                    onClick = { onStationClick(station) }
                )
            }
        }
    }
}


@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview(device = TABLET )
@Composable
private fun RadioScreenPreview(
    @PreviewParameter(RadioScreenPreviewParameterProvider::class) state: RadioScreenPreviewState
) {
    RadioTheme {
        Surface {
            RadioScreen(
                stations = state.stations,
                currentCountryCode = state.currentCountryCode,
                onStationClick = {},
                onToggleLocale = {}
            )
        }
    }
}
