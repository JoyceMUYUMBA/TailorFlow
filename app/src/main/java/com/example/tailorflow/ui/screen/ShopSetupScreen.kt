package com.example.tailorflow.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tailorflow.navigation.Screen
import com.example.tailorflow.ui.theme.*
import com.example.tailorflow.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopSetupScreen(navController: NavController, vm: ProfileViewModel = viewModel()) {
    var shopName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("FCFA") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ma Maison de Couture", color = TailorGold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TailorDarkBg)
            )
        },
        containerColor = TailorDarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Configurons votre atelier",
                style = MaterialTheme.typography.titleMedium,
                color = TailorTextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            ShopTextField(value = shopName, onValueChange = { shopName = it }, label = "Nom de la boutique")
            Spacer(modifier = Modifier.height(16.dp))
            ShopTextField(value = city, onValueChange = { city = it }, label = "Ville")
            Spacer(modifier = Modifier.height(16.dp))
            ShopTextField(value = country, onValueChange = { country = it }, label = "Pays")
            Spacer(modifier = Modifier.height(16.dp))
            ShopTextField(value = currency, onValueChange = { currency = it }, label = "Devise (ex: FCFA, €, $)")
            Spacer(modifier = Modifier.height(16.dp))
            ShopTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description ou Devise de l'atelier",
                singleLine = false
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    vm.saveShopProfile(shopName, city, country, description, currency) {
                        navController.navigate(Screen.List.route) {
                            popUpTo(Screen.ShopSetup.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = TailorGold, contentColor = TailorDarkBg),
                enabled = shopName.isNotBlank() && city.isNotBlank()
            ) {
                Text("Commencer l'aventure", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ShopTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TailorGold,
            unfocusedBorderColor = TailorCardBorder,
            focusedLabelColor = TailorGold,
            unfocusedLabelColor = TailorTextSecondary,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}
