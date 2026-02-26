package praeterii.radio.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.murgupluoglu.flagkit.FlagKit
import praeterii.radio.R
import praeterii.radio.data.RadioCountry
import praeterii.radio.theme.RadioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CountryPickerSheet(
    countries: List<RadioCountry>,
    isLoading: Boolean,
    onCountrySelect: (RadioCountry) -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
        ) {
            if (isLoading && countries.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            text = stringResource(R.string.country_picker_title),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(countries) { country ->
                        ListItem(
                            headlineContent = { Text(country.name) },
                            leadingContent = {
                                val flagId = FlagKit.getResId(country.iso_3166_1)
                                if (flagId != 0) {
                                    Image(
                                        painter = painterResource(flagId),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            trailingContent = {
                                Text(
                                    text = country.stationcount.toString(),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            modifier = Modifier.clickable {
                                onCountrySelect(country)
                            }
                        )
                        FadingDivider()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CountryPickerSheetPreview() {
    RadioTheme {
        // Content-only preview since ModalBottomSheet won't show easily in a standard preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        text = stringResource(R.string.country_picker_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                val sampleCountries = listOf(
                    RadioCountry("Poland", "PL", 123),
                    RadioCountry("United States", "US", 456),
                    RadioCountry("Germany", "DE", 789)
                )
                items(sampleCountries) { country ->
                    ListItem(
                        headlineContent = { Text(country.name) },
                        leadingContent = {
                            val flagId = FlagKit.getResId(country.iso_3166_1)
                            if (flagId != 0) {
                                Image(
                                    painter = painterResource(flagId),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        trailingContent = {
                            Text(
                                text = country.stationcount.toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        modifier = Modifier.clickable { }
                    )
                    FadingDivider()
                }
            }
        }
    }
}
