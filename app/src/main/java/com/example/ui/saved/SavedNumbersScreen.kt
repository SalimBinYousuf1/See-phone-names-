package com.example.ui.saved

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.local.entities.SavedNumberEntity
import com.example.ui.components.InitialAvatar
import com.example.ui.components.SalimHeader
import com.example.ui.components.SaveNumberDialog
import com.example.ui.theme.SalimBlue
import com.example.ui.theme.SalimCardBorder
import com.example.ui.theme.SalimGreen
import com.example.ui.theme.SalimOrange
import com.example.ui.theme.SalimSearchField
import com.example.ui.theme.SalimSurfaceSecondary
import com.example.ui.theme.SalimTextPrimary
import com.example.ui.theme.SalimTextSecondary
import com.example.ui.theme.SalimTextTertiary

@Composable
fun SavedNumbersScreen(
    savedNumbers: List<SavedNumberEntity>,
    onBack: () -> Unit,
    onSaveNumber: (normalized: String, display: String, label: String, notes: String?, category: String?, isFavorite: Boolean) -> Unit,
    onDeleteSavedNumber: (SavedNumberEntity) -> Unit,
    onLookupNumber: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var editingEntity by remember { mutableStateOf<SavedNumberEntity?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredList = remember(searchQuery, savedNumbers) {
        if (searchQuery.isBlank()) {
            savedNumbers
        } else {
            savedNumbers.filter {
                it.label.contains(searchQuery, ignoreCase = true) ||
                        it.normalizedNumber.contains(searchQuery) ||
                        it.displayNumber.contains(searchQuery) ||
                        (it.category?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SalimHeader(
                title = "Local Directory",
                subtitle = "${savedNumbers.size} saved numbers",
                onBack = onBack
            )

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, category, or number", color = SalimTextTertiary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SalimTextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("clear_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = SalimTextSecondary
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("saved_search_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SalimSearchField,
                    unfocusedContainerColor = SalimSearchField,
                    focusedBorderColor = SalimBlue,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isBlank()) "No saved numbers yet" else "No matching entries found",
                            style = MaterialTheme.typography.titleMedium,
                            color = SalimTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Save custom labels and notes to numbers in your personal local directory.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SalimTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                    items(filteredList, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLookupNumber(item.normalizedNumber) }
                                .testTag("saved_item_${item.id}"),
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
                                InitialAvatar(
                                    name = item.label,
                                    modifier = Modifier.size(46.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.label,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = SalimTextPrimary
                                        )
                                        if (item.isFavorite) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Favorite",
                                                tint = SalimOrange,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = if (item.displayNumber.isNotBlank()) item.displayNumber else item.normalizedNumber,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SalimTextSecondary
                                    )

                                    item.category?.let { cat ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.White
                                        ) {
                                            Text(
                                                text = cat,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SalimTextSecondary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    item.notes?.let { notes ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = notes,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SalimTextTertiary,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${item.normalizedNumber}")
                                            }
                                            context.startActivity(dialIntent)
                                        },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .testTag("call_saved_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = "Call",
                                            tint = SalimGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { editingEntity = item },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .testTag("edit_saved_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = SalimTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteSavedNumber(item) },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .testTag("delete_saved_${item.id}")
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
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // FAB to add new saved number
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = SalimBlue,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_saved_number_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add New Entry"
            )
        }
    }

    // Add / Edit Dialogs
    if (showAddDialog) {
        var rawNumber by remember { mutableStateOf("") }
        SaveNumberDialog(
            displayNumber = "New Phone Number",
            initialLabel = "",
            initialNotes = "",
            initialCategory = "",
            initialFavorite = false,
            onSave = { label, notes, category, isFav ->
                onSaveNumber(rawNumber.ifBlank { "+1" }, rawNumber, label, notes, category, isFav)
            },
            onDismiss = { showAddDialog = false }
        )
    }

    editingEntity?.let { entity ->
        SaveNumberDialog(
            displayNumber = entity.displayNumber.ifBlank { entity.normalizedNumber },
            initialLabel = entity.label,
            initialNotes = entity.notes ?: "",
            initialCategory = entity.category ?: "",
            initialFavorite = entity.isFavorite,
            onSave = { label, notes, category, isFav ->
                onSaveNumber(entity.normalizedNumber, entity.displayNumber, label, notes, category, isFav)
                editingEntity = null
            },
            onDismiss = { editingEntity = null }
        )
    }
}
