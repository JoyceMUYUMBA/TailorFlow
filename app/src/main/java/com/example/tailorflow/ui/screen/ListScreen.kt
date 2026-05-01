package com.example.tailorflow.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tailorflow.data.model.Commande
import com.example.tailorflow.navigation.Screen
import com.example.tailorflow.ui.components.CommandCard
import com.example.tailorflow.ui.theme.*
import com.example.tailorflow.viewmodel.CommandeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    vm: CommandeViewModel,
    navController: NavController
) {
    val commandes by vm.commandesFiltrees.collectAsState()
    val currentFiltre by vm.filtre.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()
    val stats by vm.stats.collectAsState()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    var isSearchActive by remember { mutableStateOf(false) }
    var commandeASupprimer by remember { mutableStateOf<Commande?>(null) }

    // Rafraîchir les commandes à chaque fois qu'on arrive sur l'écran
    LaunchedEffect(Unit) {
        vm.refresh()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = TailorDarkBg,
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("TailorFlow", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = TailorGold))
                    Text("Gestion d'Atelier Pro", style = MaterialTheme.typography.bodySmall, color = TailorTextSecondary)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Statistiques rapides
                    StatMiniItem("Total Commandes", stats.first.toString(), Icons.AutoMirrored.Outlined.List)
                    StatMiniItem("Terminées", stats.second.toString(), Icons.Outlined.CheckCircle)
                    StatMiniItem("Urgentes (-48h)", stats.third.toString(), Icons.Outlined.Warning, StatusUrgent)
                }
                
                HorizontalDivider(color = TailorCardBorder, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxHeight()) {
                    DrawerMenuItem(Icons.AutoMirrored.Outlined.List, "Commandes") { 
                        scope.launch { drawerState.close() } 
                    }
                    DrawerMenuItem(Icons.Outlined.AddCircleOutline, "Ajouter une commande") { 
                        vm.editCommande(null)
                        navController.navigate(Screen.Form.route)
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem(Icons.Outlined.Search, "Rechercher un client") { 
                        isSearchActive = true
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem(Icons.Outlined.BarChart, "Statistiques") { 
                        navController.navigate(Screen.Statistics.route)
                        scope.launch { drawerState.close() } 
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    DrawerMenuItem(Icons.Outlined.Settings, "Paramètre") {
                        navController.navigate(Screen.Settings.route)
                        scope.launch { drawerState.close() } 
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBarContent(
                    isSearchActive = isSearchActive,
                    searchQuery = searchQuery,
                    onSearchToggle = { isSearchActive = it },
                    onSearchQueryChange = { vm.setSearchQuery(it) },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { 
                        vm.editCommande(null)
                        navController.navigate(Screen.Form.route) 
                    },
                    containerColor = TailorGold,
                    contentColor = TailorDarkBg,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                // Filtres de statut horizontaux
                StatusFilterRow(currentFiltre) { vm.setFiltre(it) }

                if (commandes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if(searchQuery.isEmpty()) "Aucune commande" else "Aucun résultat pour \"$searchQuery\"", color = TailorTextSecondary)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(commandes) { cmd ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Surface(
                                    onClick = {
                                        vm.editCommande(cmd)
                                        navController.navigate(Screen.Form.route)
                                    },
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    CommandCard(cmd)
                                }
                                IconButton(
                                    onClick = { commandeASupprimer = cmd },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 22.dp, end = 8.dp)
                                ) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Supprimer", tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (commandeASupprimer != null) {
            DeleteConfirmationDialog(
                commande = commandeASupprimer!!,
                onConfirm = { 
                    vm.supprimer(it)
                    commandeASupprimer = null 
                },
                onDismiss = { commandeASupprimer = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchToggle: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp)) {
        if (!isSearchActive) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White) }
                Text("TailorFlow", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = TailorGold))
                IconButton(onClick = { onSearchToggle(true) }) { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) }
            }
        } else {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nom ou téléphone...", color = TailorTextSecondary) },
                leadingIcon = {
                    IconButton(onClick = { onSearchToggle(false); onSearchQueryChange("") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) { Icon(Icons.Default.Close, contentDescription = null, tint = Color.White) }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = TailorSurface,
                    unfocusedContainerColor = TailorSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = TailorGold
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun StatusFilterRow(currentFiltre: String, onFiltreChange: (String) -> Unit) {
    val filterOptions = listOf(
        "TOUT" to "Toutes",
        "URGENT" to "Urgentes",
        "En attente" to "En attente",
        "Coupe en cours" to "Coupe",
        "Couture" to "Couture",
        "Terminé" to "Terminé"
    )
    LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filterOptions) { (key, label) ->
            FilterChip(
                selected = currentFiltre == key,
                onClick = { onFiltreChange(key) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = if (key == "URGENT") StatusUrgent else TailorGold,
                    selectedLabelColor = if (key == "URGENT") Color.White else TailorDarkBg,
                    containerColor = TailorSurface,
                    labelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun StatMiniItem(label: String, value: String, icon: ImageVector, color: Color = Color.White) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = TailorTextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ColumnScope.DrawerMenuItem(icon: ImageVector, label: String, color: Color = Color.White, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null, tint = color) },
        label = { Text(label, color = color, fontSize = 16.sp) },
        selected = false,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}

@Composable
fun DeleteConfirmationDialog(commande: Commande, onConfirm: (Commande) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmer la suppression", color = Color.White, fontWeight = FontWeight.Bold) },
        text = { Text("Voulez-vous vraiment supprimer la commande de ${commande.nomClient} ?", color = TailorTextSecondary) },
        confirmButton = {
            Button(onClick = { onConfirm(commande) }, colors = ButtonDefaults.buttonColors(containerColor = StatusDelete)) {
                Text("Supprimer", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", color = TailorGold) }
        },
        containerColor = TailorSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
