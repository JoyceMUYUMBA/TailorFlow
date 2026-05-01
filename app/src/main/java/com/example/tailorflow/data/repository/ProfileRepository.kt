package com.example.tailorflow.data.repository

import com.example.tailorflow.data.model.Profile
import com.example.tailorflow.data.remote.SupabaseClientProvider
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getProfile(): Profile? = withContext(Dispatchers.IO) {
        val userId = client.auth.currentUserOrNull()?.id ?: return@withContext null
        try {
            client.postgrest["profiles"]
                .select {
                    filter { eq("id", userId) }
                }
                .decodeSingleOrNull<Profile>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveProfile(profile: Profile) = withContext(Dispatchers.IO) {
        try {
            client.postgrest["profiles"].upsert(profile)
        } catch (e: Exception) {
            throw e
        }
    }
}
