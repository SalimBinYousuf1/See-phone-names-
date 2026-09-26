package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SalimBlue
import com.example.ui.theme.SalimCardBorder
import com.example.ui.theme.SalimOrange
import com.example.ui.theme.SalimSearchField
import com.example.ui.theme.SalimTextPrimary
import com.example.ui.theme.SalimTextSecondary

@Composable
fun SaveNumberDialog(
    displayNumber: String,
    initialLabel: String = "",
    initialNotes: String = "",
    initialCategory: String = "",
    initialFavorite: Boolean = false,
    onSave: (label: String, notes: String?, category: String?, isFavorite: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var label by remember { mutableStateOf(initialLabel) }
    var notes by remember { mutableStateOf(initialNotes) }
    var category by remember { mutableStateOf(initialCategory) }
    var isFavorite by remember { mutableStateOf(initialFavorite) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Save to Local Directory",
                            style = MaterialTheme.typography.titleLarge,
                            color = SalimTextPrimary
                        )
                        Text(
                            text = displayNumber,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SalimTextSecondary
                        )
                    }

                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("toggle_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) SalimOrange else SalimTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = {
                        label = it
                        if (it.isNotBlank()) errorMessage = null
                    },
                    label = { Text("Label / Identity Name *") },
                    placeholder = { Text("e.g. Dr. Alex, Electrician, Mom") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_label_input"),
                    shape = RoundedCornerShape(12.dp),
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SalimSearchField,
                        unfocusedContainerColor = SalimSearchField,
                        focusedBorderColor = SalimBlue,
                        unfocusedBorderColor = SalimCardBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (optional)") },
                    placeholder = { Text("e.g. Personal, Work, Utility") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_category_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SalimSearchField,
                        unfocusedContainerColor = SalimSearchField,
                        focusedBorderColor = SalimBlue,
                        unfocusedBorderColor = SalimCardBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Personal Notes (optional)") },
                    placeholder = { Text("e.g. Call only after 5 PM") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SalimSearchField,
                        unfocusedContainerColor = SalimSearchField,
                        focusedBorderColor = SalimBlue,
                        unfocusedBorderColor = SalimCardBorder
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_cancel_button")
                    ) {
                        Text("Cancel", color = SalimTextSecondary)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (label.isBlank()) {
                                errorMessage = "Please enter a name or label"
                            } else {
                                onSave(label, notes.ifBlank { null }, category.ifBlank { null }, isFavorite)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SalimBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_confirm_button")
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}
