package com.kiwankim.kiwankim.myapplication3

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kiwankim.kiwankim.myapplication3.ui.navigation.AniApp
import com.kiwankim.kiwankim.myapplication3.ui.theme.AniTimeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermissionIfNeeded()

        // Re-arm reminders for saved favorites (survives app updates).
        lifecycleScope.launch {
            (application as AniApplication).container.repository.rescheduleAll()
        }

        val startAnimeNo = intent.getIntExtra(EXTRA_ANIME_NO, -1).takeIf { it > 0 }

        setContent {
            AniTimeTheme {
                AniApp(startAnimeNo = startAnimeNo)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            if (!granted) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    companion object {
        const val EXTRA_ANIME_NO = "extra_anime_no"
    }
}
