package com.example.ui.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SalimDividerView
import com.example.ui.components.SalimHeader
import com.example.ui.theme.SalimBlue
import com.example.ui.theme.SalimCardBorder
import com.example.ui.theme.SalimGreen
import com.example.ui.theme.SalimRed
import com.example.ui.theme.SalimSurfaceSecondary
import com.example.ui.theme.SalimTextPrimary
import com.example.ui.theme.SalimTextSecondary

@Composable
fun PrivacyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SalimHeader(
            title = "Privacy Principles",
            subtitle = "Transparency & Human Safety",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SalimSurfaceSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = SalimBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Standalone Lookup Architecture",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = SalimTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Salim is strictly a manual phone number identity lookup utility. You explicitly enter a number and tap lookup. The app does not operate as a dialer, does not screen incoming calls, and does not intercept system calls or monitor your phone in the background.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SalimTextSecondary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Core Privacy Guarantees",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = SalimTextPrimary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        GuaranteeItem(
                            icon = Icons.Default.Check,
                            iconColor = SalimGreen,
                            title = "Device Contacts Stay on Device",
                            description = "When contact matching is enabled, your phone book is read locally using Android's ContactsContract API. Your contacts are never transmitted to any external server."
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 12.dp))

                        GuaranteeItem(
                            icon = Icons.Default.Check,
                            iconColor = SalimGreen,
                            title = "No AI Guessing of People's Names",
                            description = "Arbitrary private individuals cannot be guessed from number patterns, area codes, or artificial intelligence algorithms. If no permitted source verifies an identity, 'No verified identity found' is reported."
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 12.dp))

                        GuaranteeItem(
                            icon = Icons.Default.Check,
                            iconColor = SalimGreen,
                            title = "Explicit Source Transparency",
                            description = "Every lookup result clearly cites where the identity was found: Saved in your contacts, Saved by you, Public business record, Community-submitted, or External directory."
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 12.dp))

                        GuaranteeItem(
                            icon = Icons.Default.Check,
                            iconColor = SalimGreen,
                            title = "Complete Deletion Rights",
                            description = "You have full control over your local search history and directory. You can delete individual records or permanently wipe all local database files with a single action."
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Strict Prohibitions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = SalimTextPrimary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SalimCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        GuaranteeItem(
                            icon = Icons.Default.Close,
                            iconColor = SalimRed,
                            title = "No People-Search or Doxxing",
                            description = "Private home addresses, personal emails, and social media profiles are never retrieved, exposed, or compiled."
                        )

                        SalimDividerView(modifier = Modifier.padding(vertical = 12.dp))

                        GuaranteeItem(
                            icon = Icons.Default.Close,
                            iconColor = SalimRed,
                            title = "No Background Call Screening",
                            description = "Salim does not implement CallScreeningService, default dialer interfaces, or system call overlays."
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun GuaranteeItem(
    icon: ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = SalimTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = SalimTextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
