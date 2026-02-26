package praeterii.radio.compose.country

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.murgupluoglu.flagkit.FlagKit
import praeterii.radio.data.RadioCountry

@Composable
internal fun CountryItem(
    country: RadioCountry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        modifier = modifier.clickable(onClick = onClick)
    )
}