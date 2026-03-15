package praeterii.radio.compose.commons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import praeterii.radio.R

@Composable
internal fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp
) {
    val scale = remember { Animatable(1f) }
    var skipInitialAnimation by remember { mutableStateOf(true) }

    LaunchedEffect(isFavorite) {
        if (isFavorite && !skipInitialAnimation) {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            scale.animateTo(1f)
        }
        skipInitialAnimation = false
    }

    val tint by animateColorAsState(
        targetValue = isFavorite.toFavoriteColor(),
        label = "favorite_color"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = isFavorite.toFavoritePainter(),
            contentDescription = isFavorite.toFavoriteContentDescription(),
            tint = tint,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value
                )
        )
    }
}

@Composable
private fun Boolean.toFavoriteColor(): Color = if (this) {
    Color.Red
} else {
    MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun Boolean.toFavoritePainter(): Painter = if (this) {
    painterResource(R.drawable.favorite_filled_24px)
} else {
    painterResource(R.drawable.favorite_24px)
}

@Composable
private fun Boolean.toFavoriteContentDescription(): String = if (this) {
    stringResource(R.string.remove_from_favorites)
} else {
    stringResource(R.string.add_to_favorites)
}
