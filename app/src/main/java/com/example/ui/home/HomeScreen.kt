package com.example.ui.home

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.entities.LookupHistoryEntity
import com.example.ui.components.CountryPickerDialog
import com.example.ui.components.SalimHeader
import com.example.ui.theme.SalimBlue
import com.example.ui.theme.SalimCardBorder
import com.example.ui.theme.SalimDivider
import com.example.ui.theme.SalimGreen
import com.example.ui.theme.SalimRed
import com.example.ui.theme.SalimSearchField
import com.example.ui.theme.SalimSurfaceSecondary
import com.example.ui.theme.SalimTextPrimary
import com.example.ui.theme.SalimTextSecondary
import com.example.ui.theme.SalimTextTertiary
import com.example.util.RegionInfo
import com.example.util.SourceFormatter

@Composable
fun HomeScreen(
    phoneInput: String,
    selectedRegion: String,
    supportedRegions: List<RegionInfo>,
    isLookingUp: Boolean,
    errorMessage: String?,
    recentHistory: List<LookupHistoryEntity>,
    useDeviceContacts: Boolean,
    onPhoneInputChanged: (String) -> Unit,
    onRegionSelected: (RegionInfo) -> Unit,
    onPaste: (String) -> Unit,
    onClearInput: () -> Unit,
    onLookup: (String?) -> Unit,
    onDeleteHistoryItem: (Long) -> Unit,
    onClearAllHistory: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onEnableContactsPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCountryPicker by remember { mutableStateOf(false) }

    val currentRegionInfo = remember(selectedRegion, supportedRegions) {
        supportedRegions.find { it.regionCode.equals(selectedRegion, ignoreCase = true) }
            ?: RegionInfo("US", 1, "United States")
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onEnableContactsPermission()
        }
    }

    val hasContactPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SalimHeader(
            title = "Salim",
            subtitle = "Number identity lookup",
            actions = {
                IconButton(
                    onClick = onNavigateToSaved,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_saved_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "Saved Directory",
                        tint = SalimTextPrimary
                    )
                }
                IconButton(
                    onClick = onNavigateToHistory,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Search History",
                        tint = SalimTextPrimary
                    )
                }
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = SalimTextPrimary
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lookup input card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lookup_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SalimSurfaceSecondary),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SalimCardBorder))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Phone number",
                            style = MaterialTheme.typography.labelLarge,
                            color = SalimTextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Country selector pill
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showCountryPicker = true }
                                    .testTag("country_selector_button"),
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${currentRegionInfo.regionCode} +${currentRegionInfo.countryCode}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = SalimTextPrimary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Country",
                                        tint = SalimTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Phone input field
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = onPhoneInputChanged,
                                placeholder = {
                                    Text(
                                        text = "Enter or paste number",
                                        color = SalimTextTertiary,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("phone_number_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = { onLookup(null) }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedBorderColor = SalimBlue,
                                    unfocusedBorderColor = SalimCardBorder
                                ),
                                trailingIcon = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (phoneInput.isNotBlank()) {
                                            IconButton(
                                                onClick = onClearInput,
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .testTag("clear_input_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear",
                                                    tint = SalimTextSecondary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        } else {
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                    val clip = clipboard?.primaryClip
                                                    if (clip != null && clip.itemCount > 0) {
                                                        val text = clip.getItemAt(0).text?.toString().orEmpty()
                                                        if (text.isNotBlank()) {
                                                            onPaste(text)
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .testTag("paste_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentPaste,
                                                    contentDescription = "Paste from clipboard",
                                                    tint = SalimBlue,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = SalimRed,
                                modifier = Modifier.testTag("error_text")
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onLookup(null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("lookup_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = SalimBlue),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLookingUp
                        ) {
                            if (isLookingUp) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Look up number",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Privacy note
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Privacy Protected",
                        tint = SalimTextTertiary,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Text(
                        text = "Global directory intelligence enabled. Identifies callers and entities via on-device contacts, verified public records, and global telecom directory intelligence indefinitely.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SalimTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            // Optional Contacts Permission Notice
            if (!hasContactPermission && useDeviceContacts) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contacts_permission_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Enable Local Contacts Matching",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = SalimTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Allow Salim to match numbers against your saved contacts strictly on-device. Contacts are never uploaded.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SalimTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SalimBlue),
                                modifier = Modifier.testTag("grant_contacts_permission_button")
                            ) {
                                Text("Allow", color = Color.White)
                            }
                        }
                    }
                }
            }

            // Recent Searches Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Searches",
                        style = MaterialTheme.typography.titleLarge,
                        color = SalimTextPrimary
                    )

                    if (recentHistory.isNotEmpty()) {
                        TextButton(
                            onClick = onClearAllHistory,
                            modifier = Modifier.testTag("clear_recent_history_button")
                        ) {
                            Text(
                                text = "Clear All",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SalimRed,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // Recent searches list
            if (recentHistory.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SalimSurfaceSecondary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No recent searches",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SalimTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter a phone number above to look up its verified identity.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SalimTextTertiary
                            )
                        }
                    }
                }
            } else {
                items(recentHistory.take(10), key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLookup(item.normalizedNumber) }
                            .testTag("history_item_${item.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SalimSurfaceSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.displayName ?: "No verified identity found",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (item.displayName != null) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = if (item.displayName != null) SalimTextPrimary else SalimTextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = item.displayNumber,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SalimTextSecondary
                                    )
                                    item.sourceName?.let { source ->
                                        Text(
                                            text = "• $source",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SalimTextTertiary
                                        )
                                    }
                                }
                            }

                            Text(
                                text = SourceFormatter.formatTimestamp(item.lookedUpAt),
                                style = MaterialTheme.typography.bodySmall,
                                color = SalimTextTertiary
                            )

                            IconButton(
                                onClick = { onDeleteHistoryItem(item.id) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("delete_history_item_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete from history",
                                    tint = SalimTextTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
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
            selectedRegion = selectedRegion,
            onRegionSelected = {
                onRegionSelected(it)
                showCountryPicker = false
            },
            onDismiss = { showCountryPicker = false }
        )
    }
}
