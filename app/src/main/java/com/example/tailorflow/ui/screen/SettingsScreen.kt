package com.example.tailorflow.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tailorflow.navigation.Screen
import com.example.tailorflow.ui.theme.*
import com.example.tailorflow.viewmodel.AuthViewModel
import com.example.tailorflow.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController, 
    authVm: AuthViewModel = viewModel(),
    profileVm: ProfileViewModel = viewModel()
) {
    val profile by profileVm.profile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètre", color = TailorGold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = TailorGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TailorDarkBg)
            )
        },
        containerColor = TailorDarkBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item { SettingsSectionTitle("Profil de la boutique") }
            item {
                // En-tête du profil avec les infos en direct
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(TailorSurface)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = TailorGold.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            Icons.Default.Store, 
                            contentDescription = null, 
                            tint = TailorGold, 
                            modifier = Modifier.padding(20.dp).fillMaxSize()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = profile?.shopName ?: "Chargement...",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "${profile?.city ?: ""}, ${profile?.country ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TailorTextSecondary
                    )
                    
                    if (!profile?.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = profile?.description ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = TailorGold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.ShopSetup.route) },
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(TailorCardBorder)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TailorGold)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Modifier le profil")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { SettingsSectionTitle("Réglages Compte") }
            item {
                SettingsItem(
                    icon = Icons.Default.CurrencyExchange,
                    title = "Devise par défaut",
                    subtitle = profile?.currency ?: "FCFA",
                    onClick = { /* TODO */ }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.People,
                    title = "Mon Équipe",
                    subtitle = "Gérer les membres (Bientôt)",
                    onClick = { /* TODO */ }
                )
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = TailorCardBorder) }
            item { SettingsSectionTitle("Application") }
            item {
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Langue",
                    subtitle = "Français",
                    onClick = { /* TODO */ }
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Déconnexion",
                    subtitle = "Quitter votre session",
                    color = StatusDelete,
                    onClick = {
                        authVm.logout()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("TailorFlow v1.0.5", color = TailorTextSecondary.copy(alpha = 0.5f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TailorGold,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color = TailorGold,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(title, color = if (color == TailorGold) Color.White else color) },
        supportingContent = { Text(subtitle, color = TailorTextSecondary) },
        leadingContent = { Icon(icon, contentDescription = null, tint = color) },
        colors = ListItemDefaults.colors(containerColor = TailorDarkBg)
    )
}
