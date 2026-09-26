package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CountryPickerDialog
import com.example.ui.components.SalimDividerView
import com.example.ui.components.SalimHeader
import com.example.ui.theme.SalimBlue
import com.example.ui.theme.SalimCardBorder
import com.example.ui.theme.SalimRed
import com.example.ui.theme.SalimSurfaceSecondary
import com.example.ui.theme.SalimTextPrimary
import com.example.ui.theme.SalimTextSecondary
import com.example.ui.theme.SalimTextTertiary
import com.example.util.RegionInfo

@Composable
fun SettingsScreen(
    useDeviceContacts: Boolean,
    askBeforeOnlineLookup: Boolean,
    enableAuthorizedProvider: Boolean,
    saveLookupHistory: Boolean,
    historyRetentionDays: Int,
    defaultCountry: String,
    hapticFeedback: Boolean,
    supportedRegions: List<RegionInfo>,
    onToggleUseDeviceContacts: (Boolean) -> Unit,
    onToggleAskBeforeOnline: (Boolean) -> Unit,
    onToggleAuthorizedProvider: (Boolean) -> Unit,
    onToggleSaveHistory: (Boolean) -> Unit,
    onSetRetentionDays: (Int) -> Unit,
    onSetDefaultCountry: (String) -> Unit,
    onToggleHapticFeedback: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    onDeleteAllData: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showCountryPicker by remember { mutableStateOf(false) }
    var showRetentionPicker by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SalimHeader(
            title = "Settings",
            subtitle = "Preferences & Privacy",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Data Sources Section
            item {
                SectionTitle("Identity Data Sources")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SalimSurfaceSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ToggleSettingRow(
                            title = "Match Device Contacts",
                            description = "Match numbers with contacts on your device. Never uploaded.",
                            checked = useDeviceContacts,
                            onCheckedChange = onToggleUseDeviceContacts,
                            testTag = "toggle_device_contacts"
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 12.dp))

                        ToggleSettingRow(
                            title = "Authorized Directory Gateway",
                            description = "Query licensed public and corporate directory partners.",
                            checked = enableAuthorizedProvider,
                            onCheckedChange = onToggleAuthorizedProvider,
                            testTag = "toggle_authorized_provider"
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 12.dp))

                        ToggleSettingRow(
                            title = "Ask Before Online Lookup",
                            description = "Always prompt before querying external directories.",
                            checked = askBeforeOnlineLookup,
                            onCheckedChange = onToggleAskBeforeOnline,
                            testTag = "toggle_ask_online"
                        )
                    }
                }
            }

            // History & Data Storage Section
            item {
                SectionTitle("History & Data Retention")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SalimSurfaceSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ToggleSettingRow(
                            title = "Save Search History",
                            description = "Record lookups locally for fast reference.",
                            checked = saveLookupHistory,
                            onCheckedChange = onToggleSaveHistory,
                            testTag = "toggle_save_history"
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 12.dp))

                        NavigationSettingRow(
                            title = "History Retention",
                            subtitle = "$historyRetentionDays days",
                            onClick = { showRetentionPicker = true },
                            testTag = "history_retention_row"
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 12.dp))

                        NavigationSettingRow(
                            title = "Default Country / Region",
                            subtitle = defaultCountry,
                            onClick = { showCountryPicker = true },
                            testTag = "default_country_row"
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 12.dp))

                        ToggleSettingRow(
                            title = "Haptic Feedback",
                            description = "Subtle vibration on search actions.",
                            checked = hapticFeedback,
                            onCheckedChange = onToggleHapticFeedback,
                            testTag = "toggle_haptic"
                        )
                    }
                }
            }

            // Privacy & Transparency Section
            item {
                SectionTitle("Privacy & Information")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SalimSurfaceSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        NavigationSettingRow(
                            title = "Privacy Principles & Architecture",
                            subtitle = "Learn how your data is protected",
                            onClick = onNavigateToPrivacy,
                            testTag = "nav_privacy_row"
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 12.dp))

                        NavigationSettingRow(
                            title = "About Salim Number Identity",
                            subtitle = "Version 1.0.0 • Pure Native Android",
                            onClick = { showAboutDialog = true },
                            testTag = "about_salim_row"
                        )
                    }
                }
            }

            // Destructive Actions Section
            item {
                SectionTitle("Data Management")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SalimSurfaceSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onClearHistory() }
                                .padding(vertical = 8.dp)
                                .testTag("clear_history_setting_btn"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Clear Search History",
                                style = MaterialTheme.typography.bodyLarge,
                                color = SalimRed,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        SalimDividerView(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDeleteAllDialog = true }
                                .padding(vertical = 8.dp)
                                .testTag("delete_all_data_btn"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Delete All Local Data",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = SalimRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showCountryPicker) {
        CountryPickerDialog(
            regions = supportedRegions,
            selectedRegion = defaultCountry,
            onRegionSelected = {
                onSetDefaultCountry(it.regionCode)
                showCountryPicker = false
            },
            onDismiss = { showCountryPicker = false }
        )
    }

    if (showRetentionPicker) {
        val options = listOf(7, 30, 90, 365)
        AlertDialog(
            onDismissRequest = { showRetentionPicker = false },
            title = { Text("History Retention Period", color = SalimTextPrimary) },
            text = {
                Column {
                    options.forEach { days ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSetRetentionDays(days)
                                    showRetentionPicker = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$days days",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (days == historyRetentionDays) SalimBlue else SalimTextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showRetentionPicker = false }) {
                    Text("Cancel", color = SalimTextSecondary)
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Delete All Local Data?", color = SalimRed) },
            text = {
                Text(
                    "This action will permanently delete all search history, saved numbers, local directory entries, and reports from this device. This cannot be undone.",
                    color = SalimTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAllData()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SalimRed)
                ) {
                    Text("Delete Everything", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel", color = SalimTextSecondary)
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Salim Number Identity", color = SalimTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "A standalone, privacy-first native Android phone number identity lookup application.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SalimTextSecondary
                    )
                    Text(
                        text = "• No silent phone call monitoring\n• Not a dialer or caller ID interceptor\n• Contacts stay strictly on your device\n• Never hallucinates or guesses personal names",
                        style = MaterialTheme.typography.bodySmall,
                        color = SalimTextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SalimBlue)
                ) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = SalimTextSecondary,
        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun ToggleSettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = SalimTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = SalimTextSecondary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SalimBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = SalimCardBorder
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun NavigationSettingRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = SalimTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SalimTextSecondary
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = SalimTextTertiary,
            modifier = Modifier.size(14.dp)
        )
    }
}
