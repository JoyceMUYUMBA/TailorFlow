package com.example.tailorflow.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tailorflow.data.model.Commande
import com.example.tailorflow.ui.theme.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun CommandCard(cmd: Commande) {
    val daysLeft = try {
        val date = LocalDate.parse(cmd.dateLivraison)
        ChronoUnit.DAYS.between(LocalDate.now(), date)
    } catch (e: Exception) {
        0L
    }
    
    val (statusColor, statusText) = when {
        cmd.statut == "Terminé" -> StatusOK to "TERMINÉ"
        cmd.isUrgent || daysLeft <= 2 -> StatusUrgent to "URGENT"
        daysLeft <= 5 -> StatusAttention to "ATTENTION"
        else -> StatusOK to "OK"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TailorSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (cmd.statut == "Terminé") StatusOK else TailorCardBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cmd.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cmd.nomClient,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = TailorTextSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = cmd.telephone, style = MaterialTheme.typography.bodySmall, color = TailorTextSecondary)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp), tint = TailorTextSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Livraison : ${cmd.dateLivraison}", style = MaterialTheme.typography.bodySmall, color = TailorTextSecondary)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    color = (if (cmd.statut == "Terminé") StatusOK else TailorGold).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(0.5.dp, if (cmd.statut == "Terminé") StatusOK else TailorGold)
                ) {
                    Text(
                        text = cmd.statut,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (cmd.statut == "Terminé") StatusOK else TailorGold
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(90.dp)
            ) {
                Surface(color = statusColor, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                
                if (cmd.statut != "Terminé") {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$daysLeft jour${if(daysLeft.toInt() != 1) "s" else ""}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (daysLeft <= 2 || cmd.isUrgent) StatusUrgent else Color.White
                            )
                        )
                        Text(text = "restant${if(daysLeft.toInt() != 1) "s" else ""}", style = MaterialTheme.typography.labelSmall, color = TailorTextSecondary)
                    }
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusOK, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}
