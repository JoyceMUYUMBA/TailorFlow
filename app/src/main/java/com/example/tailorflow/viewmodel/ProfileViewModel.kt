package com.example.tailorflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tailorflow.data.model.Profile
import com.example.tailorflow.data.repository.ProfileRepository
import com.example.tailorflow.data.remote.SupabaseClientProvider
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repo = ProfileRepository()
    private val client = SupabaseClientProvider.client

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile = _profile.asStateFlow()

    private val _isProfileComplete = MutableStateFlow<Boolean?>(null)
    val isProfileComplete = _isProfileComplete.asStateFlow()

    init {
        checkProfile()
    }

    fun checkProfile() {
        viewModelScope.launch {
            val p = repo.getProfile()
            _profile.value = p
            _isProfileComplete.value = p != null && p.shopName.isNotBlank()
        }
    }

    fun saveShopProfile(name: String, city: String, country: String, desc: String, currency: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val userId = client.auth.currentUserOrNull()?.id ?: return@launch
            val newProfile = Profile(
                id = userId,
                shopName = name,
                city = city,
                country = country,
                description = desc,
                currency = currency
            )
            try {
                repo.saveProfile(newProfile)
                _profile.value = newProfile
                _isProfileComplete.value = true
                onSuccess()
            } catch (e: Exception) {
                // Gérer l'erreur
            }
        }
    }
}
