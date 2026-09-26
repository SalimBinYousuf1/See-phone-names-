package com.example.ui.history

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entities.LookupHistoryEntity
import com.example.ui.components.SalimHeader
import com.example.ui.theme.SalimCardBorder
import com.example.ui.theme.SalimRed
import com.example.ui.theme.SalimSurfaceSecondary
import com.example.ui.theme.SalimTextPrimary
import com.example.ui.theme.SalimTextSecondary
import com.example.ui.theme.SalimTextTertiary
import com.example.util.SourceFormatter

@Composable
fun HistoryScreen(
    history: List<LookupHistoryEntity>,
    onBack: () -> Unit,
    onDeleteItem: (Long) -> Unit,
    onClearAll: () -> Unit,
    onLookupNumber: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SalimHeader(
            title = "Lookup History",
            subtitle = "${history.size} past searches",
            onBack = onBack,
            actions = {
                if (history.isNotEmpty()) {
                    TextButton(
                        onClick = { showClearConfirmation = true },
                        modifier = Modifier.testTag("clear_history_header_button")
                    ) {
                        Text(
                            text = "Clear All",
                            color = SalimRed,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        )

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No history records",
                        style = MaterialTheme.typography.titleMedium,
                        color = SalimTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Numbers you look up will be recorded here for quick access.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SalimTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(history, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLookupNumber(item.normalizedNumber) }
                            .testTag("history_screen_item_${item.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SalimSurfaceSecondary),
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
                                    text = item.displayName ?: "No verified identity found",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (item.displayName != null) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = if (item.displayName != null) SalimTextPrimary else SalimTextSecondary
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = item.displayNumber,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SalimTextSecondary
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    item.sourceName?.let { src ->
                                        Text(
                                            text = src,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SalimTextTertiary
                                        )
                                        Text(text = "•", color = SalimTextTertiary)
                                    }
                                    Text(
                                        text = SourceFormatter.formatTimestamp(item.lookedUpAt),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SalimTextTertiary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteItem(item.id) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("delete_history_screen_item_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    tint = SalimTextTertiary,
                                    modifier = Modifier.size(18.dp)
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
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Clear Lookup History?", color = SalimTextPrimary) },
            text = {
                Text(
                    "This will permanently delete all past search records from this device.",
                    color = SalimTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showClearConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SalimRed)
                ) {
                    Text("Clear All", color = androidx.compose.ui.graphics.Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirmation = false }) {
                    Text("Cancel", color = SalimTextSecondary)
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
