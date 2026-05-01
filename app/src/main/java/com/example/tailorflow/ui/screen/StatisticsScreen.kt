package com.example.tailorflow.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tailorflow.viewmodel.CommandeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(vm: CommandeViewModel, navController: NavController) {
    val stats by vm.stats.collectAsState()
    val (total, terminees, urgentes) = stats

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistiques") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Aperçu de l'activité",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                StatCard(
                    title = "Total Commandes",
                    value = total.toString(),
                    icon = Icons.Default.BarChart,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            item {
                StatCard(
                    title = "Commandes Urgentes",
                    value = urgentes.toString(),
                    icon = Icons.Default.BarChart, // Vous pouvez changer l'icône
                    color = MaterialTheme.colorScheme.error
                )
            }
            item {
                StatCard(
                    title = "Commandes Terminées",
                    value = terminees.toString(),
                    icon = Icons.Default.BarChart,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
