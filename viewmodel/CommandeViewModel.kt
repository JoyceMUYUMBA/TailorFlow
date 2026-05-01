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

    private val _commandeEnEdition = MutableStateFlow<Commande?>(null)
    val commandeEnEdition: StateFlow<Commande?> = _commandeEnEdition.asStateFlow()

    val commandes = repo.commandes

    init {
        refresh()
    }

    val commandesFiltrees = combine(commandes, _filtre) { list, filtre ->
        when (filtre) {
            "URGENT" -> list.filter {
                try {
                    val date = LocalDate.parse(it.dateLivraison)
                    ChronoUnit.DAYS.between(LocalDate.now(), date) <= 2
                } catch (e: Exception) {
                    false
                }
            }
            "Coupe en cours", "Couture", "Terminé", "En attente" -> list.filter { it.statut == filtre }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun refresh() {
        viewModelScope.launch {
            repo.fetchCommandes()
        }
    }

    fun sauvegarder(cmd: Commande) {
        viewModelScope.launch {
            repo.upsertCommande(cmd)
        }
    }

    fun supprimer(cmd: Commande) {
        viewModelScope.launch {
            repo.supprimerCommande(cmd)
        }
    }

    fun setFiltre(f: String) {
        _filtre.value = f
    }

    fun editCommande(cmd: Commande?) {
        _commandeEnEdition.value = cmd
    }
}
