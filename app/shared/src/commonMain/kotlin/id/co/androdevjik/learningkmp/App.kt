package id.co.androdevjik.learningkmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import id.co.androdevjik.learningkmp.animation.AnimationShowcaseScreen

@Composable
@Preview
fun App() {
    val darkColors = darkColorScheme(
        primary = Color(0xFF6366F1),
        secondary = Color(0xFF38BDF8),
        background = Color(0xFF0B0F19),
        surface = Color(0xFF151B2B)
    )

    MaterialTheme(colorScheme = darkColors) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0F19))
                .safeContentPadding()
        ) {
            AnimationShowcaseScreen()
        }
    }
}