package com.example

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.SalimViewModel
import com.example.ui.history.HistoryScreen
import com.example.ui.home.HomeScreen
import com.example.ui.privacy.PrivacyScreen
import com.example.ui.result.ResultScreen
import com.example.ui.saved.SavedNumbersScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.SalimTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SalimViewModel by viewModels {
        val app = application as SalimApplication
        SalimViewModel.provideFactory(app.container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalimTheme {
                SalimApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SalimApp(viewModel: SalimViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val localView = LocalView.current

    val phoneInput by viewModel.phoneInput.collectAsStateWithLifecycle()
    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    val isLookingUp by viewModel.isLookingUp.collectAsStateWithLifecycle()
    val currentResult by viewModel.currentResult.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val savedNumbers by viewModel.savedNumbers.collectAsStateWithLifecycle()

    val useDeviceContacts by viewModel.useDeviceContacts.collectAsStateWithLifecycle()
    val askBeforeOnlineLookup by viewModel.askBeforeOnlineLookup.collectAsStateWithLifecycle()
    val enableAuthorizedProvider by viewModel.enableAuthorizedProvider.collectAsStateWithLifecycle()
    val saveLookupHistory by viewModel.saveLookupHistory.collectAsStateWithLifecycle()
    val historyRetentionDays by viewModel.historyRetentionDays.collectAsStateWithLifecycle()
    val defaultCountry by viewModel.defaultCountry.collectAsStateWithLifecycle()
    val hapticFeedback by viewModel.hapticFeedback.collectAsStateWithLifecycle()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissUserMessage()
        }
    }

    fun triggerHaptic() {
        if (hapticFeedback) {
            localView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    phoneInput = phoneInput,
                    selectedRegion = selectedRegion,
                    supportedRegions = viewModel.supportedRegions,
                    isLookingUp = isLookingUp,
                    errorMessage = errorMessage,
                    recentHistory = history,
                    useDeviceContacts = useDeviceContacts,
                    onPhoneInputChanged = viewModel::onPhoneInputChanged,
                    onRegionSelected = viewModel::onRegionSelected,
                    onPaste = {
                        triggerHaptic()
                        viewModel.onPaste(it)
                    },
                    onClearInput = {
                        triggerHaptic()
                        viewModel.onClearInput()
                    },
                    onLookup = { overrideNumber ->
                        triggerHaptic()
                        viewModel.lookupNumber(overrideNumber) {
                            navController.navigate("result")
                        }
                    },
                    onDeleteHistoryItem = viewModel::deleteHistoryItem,
                    onClearAllHistory = viewModel::clearAllHistory,
                    onNavigateToSaved = {
                        triggerHaptic()
                        navController.navigate("saved")
                    },
                    onNavigateToHistory = {
                        triggerHaptic()
                        navController.navigate("history")
                    },
                    onNavigateToSettings = {
                        triggerHaptic()
                        navController.navigate("settings")
                    },
                    onEnableContactsPermission = {
                        viewModel.setUseDeviceContacts(true)
                    }
                )
            }

            composable("result") {
                currentResult?.let { merged ->
                    ResultScreen(
                        mergedResult = merged,
                        onBack = {
                            navController.popBackStack()
                        },
                        onSaveNumber = { norm, disp, label, notes, cat, isFav ->
                            triggerHaptic()
                            viewModel.saveNumber(norm, disp, label, notes, cat, isFav)
                        },
                        onSubmitReport = { norm, name, type, reason ->
                            triggerHaptic()
                            viewModel.submitReport(norm, name, type, reason)
                        }
                    )
                } ?: run {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            composable("saved") {
                SavedNumbersScreen(
                    savedNumbers = savedNumbers,
                    onBack = { navController.popBackStack() },
                    onSaveNumber = { norm, disp, label, notes, cat, isFav ->
                        triggerHaptic()
                        viewModel.saveNumber(norm, disp, label, notes, cat, isFav)
                    },
                    onDeleteSavedNumber = {
                        triggerHaptic()
                        viewModel.deleteSavedNumber(it)
                    },
                    onLookupNumber = { number ->
                        triggerHaptic()
                        viewModel.lookupNumber(number) {
                            navController.navigate("result")
                        }
                    }
                )
            }

            composable("history") {
                HistoryScreen(
                    history = history,
                    onBack = { navController.popBackStack() },
                    onDeleteItem = {
                        triggerHaptic()
                        viewModel.deleteHistoryItem(it)
                    },
                    onClearAll = {
                        triggerHaptic()
                        viewModel.clearAllHistory()
                    },
                    onLookupNumber = { number ->
                        triggerHaptic()
                        viewModel.lookupNumber(number) {
                            navController.navigate("result")
                        }
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    useDeviceContacts = useDeviceContacts,
                    askBeforeOnlineLookup = askBeforeOnlineLookup,
                    enableAuthorizedProvider = enableAuthorizedProvider,
                    saveLookupHistory = saveLookupHistory,
                    historyRetentionDays = historyRetentionDays,
                    defaultCountry = defaultCountry,
                    hapticFeedback = hapticFeedback,
                    supportedRegions = viewModel.supportedRegions,
                    onToggleUseDeviceContacts = viewModel::setUseDeviceContacts,
                    onToggleAskBeforeOnline = viewModel::setAskBeforeOnlineLookup,
                    onToggleAuthorizedProvider = viewModel::setEnableAuthorizedProvider,
                    onToggleSaveHistory = viewModel::setSaveLookupHistory,
                    onSetRetentionDays = viewModel::setHistoryRetentionDays,
                    onSetDefaultCountry = viewModel::setDefaultCountry,
                    onToggleHapticFeedback = viewModel::setHapticFeedback,
                    onClearHistory = viewModel::clearAllHistory,
                    onDeleteAllData = viewModel::deleteAllLocalData,
                    onNavigateToPrivacy = {
                        triggerHaptic()
                        navController.navigate("privacy")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("privacy") {
                PrivacyScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
