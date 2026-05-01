package com.example.tailorflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tailorflow.data.model.Commande
import com.example.tailorflow.data.repository.CommandeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CommandeViewModel : ViewModel() {

    private val repo = CommandeRepository()

    private val _filtre = MutableStateFlow("TOUT")
    val filtre: StateFlow<String> = _filtre.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _commandeEnEdition = MutableStateFlow<Commande?>(null)
    val commandeEnEdition: StateFlow<Commande?> = _commandeEnEdition.asStateFlow()

    val commandes = repo.commandes

    init {
        refresh()
    }

    val commandesFiltrees = combine(commandes, _filtre, _searchQuery) { list, filtre, query ->
        var filteredList = when (filtre) {
            "URGENT" -> list.filter {
                it.isUrgent || try {
                    val date = LocalDate.parse(it.dateLivraison)
                    ChronoUnit.DAYS.between(LocalDate.now(), date) <= 2
                } catch (e: Exception) {
                    false
                }
            }
            "Coupe en cours", "Couture", "Terminé", "En attente" -> list.filter { it.statut == filtre }
            else -> list
        }

        if (query.isNotBlank()) {
            filteredList = filteredList.filter {
                it.nomClient.contains(query, ignoreCase = true) || 
                it.telephone.contains(query)
            }
        }
        filteredList
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Statistiques calculées
    val stats = commandes.map { list ->
        val total = list.size
        val terminees = list.count { it.statut == "Terminé" }
        val urgentes = list.count {
            it.isUrgent || try {
                val date = LocalDate.parse(it.dateLivraison)
                ChronoUnit.DAYS.between(LocalDate.now(), date) <= 2
            } catch (e: Exception) {
                false
            }
        }
        Triple(total, terminees, urgentes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Triple(0, 0, 0))

    fun refresh() {
        viewModelScope.launch {
            repo.fetchCommandes()
        }
    }

    suspend fun sauvegarder(cmd: Commande) {
        repo.upsertCommande(cmd)
    }

    fun supprimer(cmd: Commande) {
        viewModelScope.launch {
            repo.supprimerCommande(cmd)
        }
    }

    fun setFiltre(f: String) {
        _filtre.value = f
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun editCommande(cmd: Commande?) {
        _commandeEnEdition.value = cmd
    }
}
