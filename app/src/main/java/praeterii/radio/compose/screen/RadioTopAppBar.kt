package praeterii.radio.compose.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.murgupluoglu.flagkit.FlagKit
import praeterii.radio.R
import praeterii.radio.theme.RadioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RadioTopAppBar(
    isSearchVisible: Boolean,
    onSearchToggle: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentCountryCode: String,
    onOpenCountryPicker: () -> Unit,
    onShowCountryPicker: () -> Unit,
    focusRequester: FocusRequester
) {
    TopAppBar(
        title = {
            AnimatedContent(
                targetState = isSearchVisible,
                transitionSpec = {
                    if (targetState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut())
                    }.using(
                        SizeTransform(clip = false)
                    )
                }, label = "search_animation"
            ) { targetIsSearchVisible ->
                if (targetIsSearchVisible) {
                    SearchBar(
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = searchQuery,
                                onQueryChange = onSearchQueryChange,
                                onSearch = { },
                                expanded = false,
                                onExpandedChange = {},
                                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                                leadingIcon = {
                                    IconButton(onClick = {
                                        onSearchToggle(false)
                                        onSearchQueryChange("")
                                    }) {
                                        Icon(
                                            painterResource(R.drawable.arrow_back_24px),
                                            contentDescription = null
                                        )
                                    }
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { onSearchQueryChange("") }) {
                                            Icon(
                                                painterResource(R.drawable.close_24px),
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            )
                        },
                        expanded = false,
                        onExpandedChange = {},
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                    ) {}
                } else {
                    Text(stringResource(R.string.app_name))
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        actions = {
            AnimatedVisibility(
                visible = !isSearchVisible,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row {
                    IconButton(onClick = { onSearchToggle(true) }) {
                        Icon(
                            painter = painterResource(R.drawable.search_24px),
                            contentDescription = "Search"
                        )
                    }

                    val flagResId = FlagKit.getResId(currentCountryCode)
                    if (flagResId != 0) {
                        IconButton(onClick = {
                            onOpenCountryPicker()
                            onShowCountryPicker()
                        }) {
                            Image(
                                painter = painterResource(flagResId),
                                contentDescription = "Select Country",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun RadioTopAppBarPreview() {
    RadioTheme {
        Surface {
            RadioTopAppBar(
                isSearchVisible = false,
                onSearchToggle = {},
                searchQuery = "",
                onSearchQueryChange = {},
                currentCountryCode = "PL",
                onOpenCountryPicker = {},
                onShowCountryPicker = {},
                focusRequester = remember { FocusRequester() }
            )
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun RadioTopAppBarSearchPreview() {
    RadioTheme {
        Surface {
            RadioTopAppBar(
                isSearchVisible = true,
                onSearchToggle = {},
                searchQuery = "Radio",
                onSearchQueryChange = {},
                currentCountryCode = "PL",
                onOpenCountryPicker = {},
                onShowCountryPicker = {},
                focusRequester = remember { FocusRequester() }
            )
        }
    }
}
