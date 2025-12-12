package com.example.crikstats

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.example.crikstats.ui.theme.CrikStatsTheme
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var splitInstallManager: SplitInstallManager
    private val moduleInstallState = mutableStateOf(false)

    companion object {
        private const val PREFS_NAME = "crikstats_prefs"
        private const val KEY_MODULE_DOWNLOADED = "module_downloaded"
    }

    private val installListener = SplitInstallStateUpdatedListener { state ->
        when (state.status()) {
            SplitInstallSessionStatus.INSTALLED -> {
                saveModuleDownloadedState(true)
                updateModuleState()
                showToast("Module installed successfully!")
            }
            SplitInstallSessionStatus.DOWNLOADING -> {
                val progress = calculateDownloadProgress(state)
                if (progress > 0) showToast("Downloading: $progress%")
            }
            SplitInstallSessionStatus.FAILED -> {
                updateModuleState()
                showToast("Download failed: ${state.errorCode()}")
            }
            SplitInstallSessionStatus.INSTALLING -> {
                showToast("Installing module...")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splitInstallManager = SplitInstallManagerFactory.create(this)
        initializeModuleState()

        setContent {
            CrikStatsTheme {
                HomeScreen(
                    isModuleInstalled = moduleInstallState.value,
                    onDownloadModule = ::downloadModule,
                    onOpenPlayerStats = ::openPlayerStats
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        splitInstallManager.registerListener(installListener)
        updateModuleState()
    }

    override fun onPause() {
        super.onPause()
        splitInstallManager.unregisterListener(installListener)
    }

    private fun initializeModuleState() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        if (!prefs.contains(KEY_MODULE_DOWNLOADED)) {
            prefs.edit().putBoolean(KEY_MODULE_DOWNLOADED, false).apply()
        }
        moduleInstallState.value = isModuleDownloaded()
    }

    private fun updateModuleState() {
        moduleInstallState.value = isModuleDownloaded()
    }

    private fun isModuleDownloaded(): Boolean {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getBoolean(KEY_MODULE_DOWNLOADED, false)
    }

    private fun saveModuleDownloadedState(downloaded: Boolean) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_MODULE_DOWNLOADED, downloaded)
            .apply()
    }

    private fun downloadModule() {
        val request = SplitInstallRequest.newBuilder()
            .addModule("featureplayer")
            .build()

        splitInstallManager.startInstall(request)
            .addOnSuccessListener { showToast("Starting download...") }
            .addOnFailureListener { exception ->
                showToast("Failed to start download: ${exception.message}")
            }
    }

    private fun openPlayerStats() {
        if (!isModuleDownloaded()) {
            showToast("Please download the module first")
            return
        }

        try {
            val intent = Intent().setClassName(
                this,
                "com.example.feature_player.presentation.PlayerStatsActivity"
            )
            startActivity(intent)
        } catch (e: Exception) {
            showToast("Error opening module: ${e.message}")
        }
    }

    private fun calculateDownloadProgress(state: SplitInstallSessionState): Int {
        val totalBytes = state.totalBytesToDownload()
        return if (totalBytes > 0) {
            (state.bytesDownloaded() * 100 / totalBytes).toInt()
        } else 0
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}