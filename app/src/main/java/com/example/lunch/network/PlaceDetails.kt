package com.example.lunch.network

import com.google.gson.annotations.SerializedName

data class PlaceDetails(
    val name: String?,
    @SerializedName("formatted_address")
    val address: String?,
)