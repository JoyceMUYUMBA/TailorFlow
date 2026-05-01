package com.example.tailorflow.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("id")
    val id: String,
    @SerialName("shop_name")
    val shopName: String = "",
    @SerialName("city")
    val city: String = "",
    @SerialName("country")
    val country: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("currency")
    val currency: String = "FCFA"
)
