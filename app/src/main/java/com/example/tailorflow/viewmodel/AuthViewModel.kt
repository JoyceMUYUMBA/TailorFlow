package com.example.tailorflow.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tailorflow.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repo.signUp(email, password)
                onSuccess()
            } catch (e: Exception) {
                Log.e("AUTH", "Signup error", e)
                _error.value = e.localizedMessage ?: "Erreur d'inscription"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repo.signIn(email, password)
                onSuccess()
            } catch (e: Exception) {
                Log.e("AUTH", "Signin error", e)
                _error.value = e.localizedMessage ?: "Erreur de connexion"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repo.logout()
            } catch (e: Exception) {
                Log.e("AUTH", "Logout error", e)
            }
        }
    }
}
