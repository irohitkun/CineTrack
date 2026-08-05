package com.cinetrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cinetrack.app.navigation.CineTrackApp
import com.cinetrack.app.ui.theme.CineTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Dark mode by default; user preference wired in Settings (Phase 7).
            CineTrackTheme(darkTheme = true) {
                CineTrackApp()
            }
        }
    }
}
