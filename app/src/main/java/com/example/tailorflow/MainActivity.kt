package com.example.tailorflow

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tailorflow.data.remote.SupabaseClientProvider
import com.example.tailorflow.navigation.AppNavigation
import com.example.tailorflow.ui.theme.TailorFlowTheme
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Gérer le lien profond au démarrage si l'app était fermée
        intent?.let { handleSupabaseDeepLink(it) }

        setContent {
            TailorFlowTheme {
                AppNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSupabaseDeepLink(intent)
    }

    private fun handleSupabaseDeepLink(intent: Intent) {
        // En version 2.x de Supabase-kt, on utilise handleDeeplinks(intent) sur le client
        try {
            SupabaseClientProvider.client.handleDeeplinks(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
