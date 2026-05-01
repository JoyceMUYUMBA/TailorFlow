package com.example.tailorflow.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClientProvider {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://reicenitimhhycrfsnwr.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJlaWNlbml0aW1oaHljcmZzbndyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzczMzMxNjIsImV4cCI6MjA5MjkwOTE2Mn0.C0da4W9wfYHpBD83uqY6U0gxx6b0XaNpA2uXMrxkKnI"
    ) {
        install(Postgrest) {
            serializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                encodeDefaults = true // Indispensable pour que les champs par défaut soient envoyés aux RLS
            })
        }
        install(Storage)
        install(Auth)
    }
}
