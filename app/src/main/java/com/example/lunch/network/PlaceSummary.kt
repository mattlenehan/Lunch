package com.example.lunch.network

import com.google.gson.annotations.SerializedName

data class PlaceSummary(
    @SerializedName("place_id")
    val placeId: String,
    val name: String?,
    val types: List<String>?,
    val photos: List<GooglePhoto>?
)

data class GooglePhoto(
    @SerializedName("photo_reference")
    val photoReference: String,
    val width: Int?,
    val height: Int?
)