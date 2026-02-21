package praeterii.radio

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.r.cohen.radiobrowserandroid.models.RadioBrowserStation
import praeterii.radio.ui.theme.RadioTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RadioTheme {
                Surface {
                    RadioApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RadioApp(viewModel: RadioViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        val countryCode = Locale.getDefault().country
        viewModel.loadStations(countryCode.ifBlank { "US" })
    }
    RadioScreen(
        stations = viewModel.stations,
        onStationClick = { viewModel.playStation(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RadioScreen(
    stations: List<RadioBrowserStation>,
    onStationClick: (RadioBrowserStation) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Radio") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
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

@Composable
private fun StationItem(
    station: RadioBrowserStation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = station.favicon,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .padding(end = 8.dp)
        )
        Text(text = station.name)
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun RadioScreenPreview() {
    RadioTheme {
        Surface {
            RadioScreen(
                stations = listOf(
                    RadioBrowserStation(
                        stationuuid = "uuid1",
                        name = "Example Station 1",
                        url = "http://example.com/1",
                        url_resolved = "http://example.com/1",
                        homepage = "http://example.com",
                        favicon = "http://example.com/favicon.ico",
                        tags = "tag1, tag2",
                        country = "US",
                        language = "en"
                    )
                ),
                onStationClick = {}
            )
        }
    }
}
