package com.example.tailorflow.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tailorflow.navigation.Screen
import com.example.tailorflow.ui.theme.*
import com.example.tailorflow.viewmodel.AuthViewModel

@Composable
fun AuthScreen(vm: AuthViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showEmailSentMessage by remember { mutableStateOf(false) }
    
    val error by vm.error.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = TailorDarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TailorFlow",
                style = MaterialTheme.typography.headlineLarge,
                color = TailorGold,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Connexion à votre atelier",
                style = MaterialTheme.typography.bodyMedium,
                color = TailorTextSecondary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (showEmailSentMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = TailorSurface)
                ) {
                    Text(
                        text = "📧 Un e-mail de confirmation a été envoyé. Veuillez vérifier votre boîte de réception avant de vous connecter.",
                        color = TailorGold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TailorGold,
                    unfocusedBorderColor = TailorCardBorder,
                    focusedLabelColor = TailorGold,
                    unfocusedLabelColor = TailorTextSecondary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = TailorGold)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TailorGold,
                    unfocusedBorderColor = TailorCardBorder,
                    focusedLabelColor = TailorGold,
                    unfocusedLabelColor = TailorTextSecondary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    vm.signIn(email, password) {
                        navController.navigate(Screen.List.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TailorGold, contentColor = TailorDarkBg)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TailorDarkBg, strokeWidth = 2.dp)
                } else {
                    Text("Se connecter", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    vm.signUp(email, password) {
                        showEmailSentMessage = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    Text("Patientez...", color = TailorTextSecondary)
                } else {
                    Text("Créer un compte", color = TailorGold)
                }
            }
        }
    }
}
