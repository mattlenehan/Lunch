package com.example.lunch.repository

import com.example.lunch.network.PlaceDetailsResponse
import com.example.lunch.network.PlacesApiService
import com.example.lunch.network.PlacesSummaryResponse
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RestaurantRepository @Inject constructor(
    private val placesApiService: PlacesApiService
) {
    fun getRestaurants(
        query: String?,
        userLocation: LatLng
    ): Call<PlacesSummaryResponse> {
        return placesApiService.getRestaurants(
            "AIzaSyDQSd210wKX_7cz9MELkxhaEOUhFP0AkSk",
            query ?: "",
            "restaurant",
            "${userLocation.latitude},${userLocation.longitude}",
            30.0
        )
    }

    fun getRestaurant(
        id: String
    ): Call<PlaceDetailsResponse> {
        return placesApiService.getRestaurant(
            key = "AIzaSyDQSd210wKX_7cz9MELkxhaEOUhFP0AkSk",
            placeId = id
        )
    }
}