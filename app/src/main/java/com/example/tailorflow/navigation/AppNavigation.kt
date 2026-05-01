package com.example.tailorflow.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.tailorflow.ui.screen.*
import com.example.tailorflow.viewmodel.CommandeViewModel
import com.example.tailorflow.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tailorflow.data.remote.SupabaseClientProvider
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.SessionStatus

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val commandeVm: CommandeViewModel = viewModel()
    val profileVm: ProfileViewModel = viewModel()
    
    val sessionStatus by SupabaseClientProvider.client.auth.sessionStatus.collectAsState()
    val isProfileComplete by profileVm.isProfileComplete.collectAsState()

    // Recharger les données quand la session change
    LaunchedEffect(sessionStatus) {
        if (sessionStatus is SessionStatus.Authenticated) {
            profileVm.checkProfile()
            commandeVm.refresh()
        }
    }

    // Redirection automatique si le profil est incomplet
    LaunchedEffect(isProfileComplete) {
        if (sessionStatus is SessionStatus.Authenticated && isProfileComplete == false) {
            navController.navigate(Screen.ShopSetup.route) {
                popUpTo(Screen.List.route) { inclusive = true }
            }
        }
    }

    val startDestination = when {
        sessionStatus !is SessionStatus.Authenticated -> Screen.Auth.route
        isProfileComplete == false -> Screen.ShopSetup.route
        else -> Screen.List.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(vm = viewModel(), navController = navController)
        }

        composable(Screen.ShopSetup.route) {
            ShopSetupScreen(navController, profileVm)
        }

        composable(Screen.List.route) {
            ListScreen(commandeVm, navController)
        }

        composable(Screen.Form.route) {
            FormScreen(commandeVm, navController)
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(commandeVm, navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController, profileVm = profileVm)
        }
    }
}
