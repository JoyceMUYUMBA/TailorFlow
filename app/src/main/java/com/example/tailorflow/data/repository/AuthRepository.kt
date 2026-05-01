package com.example.tailorflow.data.repository

import com.example.tailorflow.data.remote.SupabaseClientProvider
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

class AuthRepository {

    private val client = SupabaseClientProvider.client

    suspend fun signUp(email: String, password: String) {
        client.auth.signUpWith(
            provider = Email,
            redirectUrl = "tailorflow://auth/callback"
        ) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signIn(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logout() {
        client.auth.signOut()
    }
}
