package com.example.lunch.network

import com.google.gson.annotations.SerializedName

data class PlacesSummaryResponse(
    @SerializedName("results")
    val results: List<PlaceSummary>
)