package com.example.tailorflow.data.repository

import android.util.Log
import com.example.tailorflow.data.model.Commande
import com.example.tailorflow.data.remote.SupabaseClientProvider
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class CommandeRepository {

    private val _commandes = MutableStateFlow<List<Commande>>(emptyList())
    val commandes: StateFlow<List<Commande>> = _commandes

    suspend fun fetchCommandes() {
        withContext(Dispatchers.IO) {
            try {
                val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                if (userId == null) {
                    _commandes.value = emptyList()
                    return@withContext
                }

                val results = SupabaseClientProvider.client.postgrest["commandes_couture"]
                    .select {
                        filter { eq("user_id", userId) }
                    }
                    .decodeList<Commande>()

                _commandes.value = results
            } catch (e: Exception) {
                Log.e("REPO", "Erreur lecture: ${e.message}")
            }
        }
    }

    suspend fun upsertCommande(cmd: Commande) {
        withContext(Dispatchers.IO) {
            try {
                val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id 
                    ?: throw Exception("Utilisateur non connecté")
                
                // Construction précise du JSON pour respecter les types Postgres (float8, bool, uuid)
                val json = buildJsonObject {
                    put("user_id", userId)
                    put("nom_client", cmd.nomClient)
                    put("telephone", cmd.telephone)
                    put("epaules", cmd.epaules.toDouble())
                    put("poitrine", cmd.poitrine.toDouble())
                    put("taille", cmd.taille.toDouble())
                    put("longueur", cmd.longueur.toDouble())
                    put("manches", cmd.manches.toDouble())
                    put("hanche", cmd.hanche.toDouble())
                    put("image_url", cmd.imageUrl)
                    put("date_livraison", cmd.dateLivraison)
                    put("statut", cmd.statut)
                    put("is_urgent", cmd.isUrgent)
                    
                    // N'envoyer l'ID que s'il est non-null et non-vide (pour UPDATE)
                    if (!cmd.id.isNullOrBlank()) {
                        put("id", cmd.id)
                    }
                }

                if (cmd.id.isNullOrBlank()) {
                    // Insertion d'une nouvelle ligne
                    SupabaseClientProvider.client.postgrest["commandes_couture"]
                        .insert(json)
                } else {
                    // Mise à jour d'une ligne existante
                    SupabaseClientProvider.client.postgrest["commandes_couture"]
                        .update(json) {
                            filter { eq("id", cmd.id) }
                        }
                }
                fetchCommandes()
            } catch (e: Exception) {
                Log.e("REPO", "Erreur upsert détaillé: ${e.message}")
                throw e
            }
        }
    }

    suspend fun supprimerCommande(cmd: Commande) {
        withContext(Dispatchers.IO) {
            try {
                cmd.id?.let { id ->
                    SupabaseClientProvider.client.postgrest["commandes_couture"]
                        .delete {
                            filter { eq("id", id) }
                        }
                }
                fetchCommandes()
            } catch (e: Exception) {
                Log.e("REPO", "Erreur suppression: ${e.message}")
            }
        }
    }
}
