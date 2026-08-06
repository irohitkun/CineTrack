package com.cinetrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cinetrack.app.data.preferences.SettingsPreferences
import com.cinetrack.app.navigation.CineTrackApp
import com.cinetrack.app.ui.theme.CineTrackTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme by settingsPreferences.darkTheme.collectAsStateWithLifecycle(initialValue = true)
            CineTrackTheme(darkTheme = darkTheme) {
                CineTrackApp()
            }
        }
    }
}
