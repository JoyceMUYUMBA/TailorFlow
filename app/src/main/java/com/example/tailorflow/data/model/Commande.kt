package com.example.tailorflow.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Commande(
    @SerialName("id")
    val id: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("nom_client")
    val nomClient: String = "",
    @SerialName("telephone")
    val telephone: String = "",
    @SerialName("epaules")
    val epaules: Float = 0f,
    @SerialName("poitrine")
    val poitrine: Float = 0f,
    @SerialName("taille")
    val taille: Float = 0f,
    @SerialName("longueur")
    val longueur: Float = 0f,
    @SerialName("manches")
    val manches: Float = 0f,
    @SerialName("hanche")
    val hanche: Float = 0f,
    @SerialName("image_url")
    val imageUrl: String = "",
    @SerialName("date_livraison")
    val dateLivraison: String = "",
    @SerialName("statut")
    val statut: String = "En attente",
    @SerialName("is_urgent")
    val isUrgent: Boolean = false
)
