package com.example.ui.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallerIdentityResult
import com.example.data.model.IdentityType
import com.example.data.model.LookupSourceType
import com.example.lookup.MergedLookupResult
import com.example.ui.components.ConfidenceBadge
import com.example.ui.components.InitialAvatar
import com.example.ui.components.ReportDialog
import com.example.ui.components.SalimDividerView
import com.example.ui.components.SalimHeader
import com.example.ui.components.SaveNumberDialog
import com.example.ui.theme.SalimBlue
import com.example.ui.theme.SalimCardBorder
import com.example.ui.theme.SalimGreen
import com.example.ui.theme.SalimOrange
import com.example.ui.theme.SalimRed
import com.example.ui.theme.SalimSurfaceSecondary
import com.example.ui.theme.SalimTextPrimary
import com.example.ui.theme.SalimTextSecondary
import com.example.ui.theme.SalimTextTertiary
import com.example.util.SourceFormatter

@Composable
fun ResultScreen(
    mergedResult: MergedLookupResult,
    onBack: () -> Unit,
    onSaveNumber: (normalized: String, display: String, label: String, notes: String?, category: String?, isFavorite: Boolean) -> Unit,
    onSubmitReport: (normalized: String, name: String?, type: String, reason: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primary = mergedResult.primaryResult
    val isKnown = primary.displayName != null || primary.businessName != null

    var showSaveDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SalimHeader(
            title = "Identity Result",
            subtitle = primary.displayNumber,
            onBack = onBack,
            actions = {
                IconButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Phone: ${primary.displayNumber}\nIdentity: ${primary.displayName ?: "No verified identity"}\nSource: ${primary.sourceName ?: "Salim Identity"}"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Number Identity"))
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("share_result_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
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
            // Main Identity Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("result_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SalimSurfaceSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        InitialAvatar(
                            name = primary.displayName ?: primary.businessName ?: "?",
                            modifier = Modifier.size(72.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = primary.displayName ?: primary.businessName ?: "No verified identity found",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = SalimTextPrimary,
                            modifier = Modifier.testTag("result_display_name")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = primary.displayNumber,
                            style = MaterialTheme.typography.bodyLarge,
                            color = SalimTextSecondary,
                            modifier = Modifier.testTag("result_phone_number")
                        )

                        if (primary.countryCode != null || primary.numberType != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            val meta = listOfNotNull(
                                primary.region?.let { "Region: $it" },
                                primary.numberType?.let { "Type: $it" }
                            ).joinToString(" • ")
                            if (meta.isNotBlank()) {
                                Text(
                                    text = meta,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SalimTextTertiary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ConfidenceBadge(result = primary)

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action buttons row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Call Action
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${primary.normalizedNumber}")
                                            }
                                            context.startActivity(dialIntent)
                                        }
                                        .testTag("call_action_button"),
                                    color = SalimGreen,
                                    shape = CircleShape
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = "Call",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Call",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SalimTextPrimary
                                )
                            }

                            // Message Action
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("smsto:${primary.normalizedNumber}")
                                            }
                                            context.startActivity(smsIntent)
                                        }
                                        .testTag("message_action_button"),
                                    color = SalimBlue,
                                    shape = CircleShape
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Message,
                                            contentDescription = "Message",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Message",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SalimTextPrimary
                                )
                            }

                            // Copy Number Action
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Phone Number", primary.normalizedNumber)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Number copied to clipboard", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("copy_action_button"),
                                    color = Color.White,
                                    shape = CircleShape,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Number",
                                            tint = SalimTextPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Copy",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SalimTextPrimary
                                )
                            }

                            // Save to Directory Action
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .clickable { showSaveDialog = true }
                                        .testTag("save_directory_action_button"),
                                    color = Color.White,
                                    shape = CircleShape,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (primary.isSavedByUser) Icons.Filled.Edit else Icons.Outlined.BookmarkAdd,
                                            contentDescription = "Save / Edit Label",
                                            tint = SalimBlue,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (primary.isSavedByUser) "Edit" else "Save",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SalimTextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Conflict Warning Card if multiple legitimate sources disagree!
            if (mergedResult.hasConflict) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("conflict_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SalimOrange.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Conflicting records",
                                    tint = SalimOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Conflicting Records Found",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = SalimTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = mergedResult.conflictExplanation ?: "Multiple sources report different identities for this number. Salim does not guess.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SalimTextSecondary
                            )

                            if (mergedResult.alternativeMatches.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Other recorded sources:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SalimTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                mergedResult.alternativeMatches.forEach { alt ->
                                    Text(
                                        text = "• ${alt.displayName ?: "Unknown"} (${alt.sourceName})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SalimTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Source & Verification Transparency Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Source Information",
                            style = MaterialTheme.typography.titleMedium,
                            color = SalimTextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        DetailRow(
                            label = "Primary Source",
                            value = primary.sourceName ?: "None (Unverified)"
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 8.dp))

                        DetailRow(
                            label = "Source Type",
                            value = when (primary.sourceType) {
                                LookupSourceType.DEVICE_CONTACTS -> "On-Device Address Book (Local Only)"
                                LookupSourceType.LOCAL_USER_DIRECTORY -> "Personal Local Directory (User Saved)"
                                LookupSourceType.PUBLIC_BUSINESS_DIRECTORY -> "Official Public Business Registry"
                                LookupSourceType.COMMUNITY_DIRECTORY -> "Consented Community Submission"
                                LookupSourceType.LICENSED_PROVIDER -> "Authorized Licensed Directory Gateway"
                                LookupSourceType.NONE -> "No Verified Source"
                            }
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 8.dp))

                        DetailRow(
                            label = "Verification Status",
                            value = if (primary.isVerified) "Verified record from authenticated source" else "Unverified identity"
                        )

                        if (primary.checkedAt > 0) {
                            SalimDividerView(modifier = Modifier.padding(vertical = 8.dp))
                            DetailRow(
                                label = "Last Checked",
                                value = SourceFormatter.formatDetailedDate(primary.checkedAt)
                            )
                        }

                        if (primary.explanation.isNotBlank()) {
                            SalimDividerView(modifier = Modifier.padding(vertical = 8.dp))
                            DetailRow(
                                label = "Explanation",
                                value = primary.explanation
                            )
                        }
                    }
                }
            }

            // Public Business / Community Details Card
            if (primary.category != null || primary.address != null || primary.website != null || primary.notes != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "Additional Details",
                                style = MaterialTheme.typography.titleMedium,
                                color = SalimTextPrimary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            primary.category?.let { cat ->
                                DetailRow(label = "Category", value = cat)
                                SalimDividerView(modifier = Modifier.padding(vertical = 8.dp))
                            }

                            primary.address?.let { addr ->
                                DetailRow(label = "Public Address", value = addr)
                                SalimDividerView(modifier = Modifier.padding(vertical = 8.dp))
                            }

                            primary.website?.let { web ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(web))
                                            context.startActivity(webIntent)
                                        }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Website",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SalimTextSecondary
                                    )
                                    Text(
                                        text = web,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = SalimBlue,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                                SalimDividerView(modifier = Modifier.padding(vertical = 8.dp))
                            }

                            primary.notes?.let { note ->
                                DetailRow(label = "Notes", value = note)
                            }
                        }
                    }
                }
            }

            // Report / Dispute Button
            item {
                OutlinedButton(
                    onClick = { showReportDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_issue_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SalimTextSecondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SalimTextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isKnown) "Report Incorrect Result or Scam" else "Report Suspicious Number",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showSaveDialog) {
        SaveNumberDialog(
            displayNumber = primary.displayNumber,
            initialLabel = primary.displayName ?: "",
            initialNotes = primary.notes ?: "",
            initialCategory = primary.category ?: "",
            initialFavorite = false,
            onSave = { label, notes, category, isFav ->
                onSaveNumber(primary.normalizedNumber, primary.displayNumber, label, notes, category, isFav)
            },
            onDismiss = { showSaveDialog = false }
        )
    }

    if (showReportDialog) {
        ReportDialog(
            number = primary.displayNumber,
            reportedName = primary.displayName,
            onSubmit = { type, desc ->
                onSubmitReport(primary.normalizedNumber, primary.displayName, type, desc)
            },
            onDismiss = { showReportDialog = false }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = SalimTextSecondary,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = SalimTextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}
