package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.ui.theme.SalimRed
import com.example.ui.theme.SalimSearchField
import com.example.ui.theme.SalimTextPrimary
import com.example.ui.theme.SalimTextSecondary

@Composable
fun ReportDialog(
    number: String,
    reportedName: String?,
    onSubmit: (reportType: String, description: String) -> Unit,
    onDismiss: () -> Unit
) {
    val reportOptions = listOf(
        "INCORRECT_IDENTITY" to "Incorrect identity information",
        "SPAM_OR_SCAM" to "Suspicious, scam, or harassment caller",
        "DISPUTE_RECORD" to "Dispute this public/community record",
        "REMOVAL_REQUEST" to "Request removal of listing"
    )

    var selectedType by remember { mutableStateOf(reportOptions.first().first) }
    var description by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

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
                Text(
                    text = "Report or Dispute Record",
                    style = MaterialTheme.typography.titleLarge,
                    color = SalimTextPrimary
                )
                Text(
                    text = "$number ${reportedName?.let { "($it)" } ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SalimTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Reason for report:",
                    style = MaterialTheme.typography.labelLarge,
                    color = SalimTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                reportOptions.forEach { (typeKey, title) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = typeKey }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == typeKey,
                            onClick = { selectedType = typeKey },
                            colors = RadioButtonDefaults.colors(selectedColor = SalimBlue)
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SalimTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        if (it.isNotBlank()) error = null
                    },
                    label = { Text("Details & Description *") },
                    placeholder = { Text("Explain why this record is incorrect or suspicious") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_description_input"),
                    shape = RoundedCornerShape(12.dp),
                    isError = error != null,
                    supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SalimSearchField,
                        unfocusedContainerColor = SalimSearchField,
                        focusedBorderColor = SalimBlue,
                        unfocusedBorderColor = SalimCardBorder
                    ),
                    maxLines = 4
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
                        modifier = Modifier.testTag("report_cancel_button")
                    ) {
                        Text("Cancel", color = SalimTextSecondary)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (description.isBlank()) {
                                error = "Please provide an explanation"
                            } else {
                                onSubmit(selectedType, description.trim())
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SalimRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("report_submit_button")
                    ) {
                        Text("Submit Report", color = Color.White)
                    }
                }
            }
        }
    }
}
