package com.example.crikstats

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crikstats.ui.theme.CrikStatsTheme
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var splitInstallManager: SplitInstallManager
    private var sessionId = 0

    companion object {
        private const val PREFS_NAME = "crikstats_prefs"
        private const val KEY_MODULE_INSTALLED = "module_installed"
        private const val KEY_APP_INITIALIZED = "app_initialized"
    }

    private val listener = SplitInstallStateUpdatedListener { state ->
        when (state.status()) {
            SplitInstallSessionStatus.INSTALLED -> {
                saveModuleInstallState(true)
                Toast.makeText(this, "Module installed successfully!", Toast.LENGTH_SHORT).show()
            }
            SplitInstallSessionStatus.DOWNLOADING -> {
                val progress = (state.bytesDownloaded() * 100 / state.totalBytesToDownload()).toInt()
                Toast.makeText(this, "Downloading: $progress%", Toast.LENGTH_SHORT).show()
            }
            SplitInstallSessionStatus.FAILED -> {
                Toast.makeText(this, "Download failed: ${state.errorCode()}", Toast.LENGTH_SHORT).show()
            }
            SplitInstallSessionStatus.INSTALLING -> {
                Toast.makeText(this, "Installing module...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splitInstallManager = SplitInstallManagerFactory.create(this)

        validateModuleState()

        setContent {
            CrikStatsTheme {
                val isModuleInstalled = remember {
                    mutableStateOf(getModuleInstallState())
                }

                HomeScreen(
                    isModuleInstalled = isModuleInstalled.value,
                    onDownloadModule = {
                        downloadPlayerModule()
                        GlobalScope.launch {
                            delay(2000)
                            isModuleInstalled.value = getModuleInstallState()
                        }
                    },
                    onOpenPlayerStats = { openPlayerStats() }
                )
            }
        }
    }

    private fun validateModuleState() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isInitialized = prefs.getBoolean(KEY_APP_INITIALIZED, false)

        if (!isInitialized) {
            prefs.edit()
                .putBoolean(KEY_APP_INITIALIZED, true)
                .putBoolean(KEY_MODULE_INSTALLED, false)
                .apply()
        }
    }

    private fun getModuleInstallState(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getBoolean(KEY_MODULE_INSTALLED, false)
    }

    private fun saveModuleInstallState(installed: Boolean) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_MODULE_INSTALLED, installed).apply()
    }

    override fun onResume() {
        super.onResume()
        splitInstallManager.registerListener(listener)
    }

    override fun onPause() {
        super.onPause()
        splitInstallManager.unregisterListener(listener)
    }

    private fun downloadPlayerModule() {
        val request = SplitInstallRequest.newBuilder()
            .addModule("featureplayer")
            .build()

        splitInstallManager.startInstall(request)
            .addOnSuccessListener { id ->
                sessionId = id
                Toast.makeText(this, "Starting download...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to start download: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun openPlayerStats() {
        if (getModuleInstallState()) {
            try {
                val intent = Intent().setClassName(
                    this,
                    "com.example.feature_player.presentation.PlayerStatsActivity"
                )
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error opening module: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Please download the module first", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isModuleInstalled: Boolean,
    onDownloadModule: () -> Unit,
    onOpenPlayerStats: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CrikStats") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Sports,
                contentDescription = "Cricket Icon",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to CrikStats",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cricket Statistics at your fingertips",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Player Stats Module",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isModuleInstalled) "Installed ✓" else "Not Installed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isModuleInstalled)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!isModuleInstalled) {
                        Button(
                            onClick = onDownloadModule,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download Player Stats Module")
                        }
                    } else {
                        Button(
                            onClick = onOpenPlayerStats,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Open"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Player Stats")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "About This App",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This app demonstrates Dynamic Feature Modules with Dagger Hilt dependency injection. Features are downloaded on-demand to reduce initial app size.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
