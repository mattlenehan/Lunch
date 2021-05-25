package com.example.lunch.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {

    @GET("textsearch/json")
    fun getRestaurants(
        @Query("key") key: String,
        @Query("input") input: String?,
        @Query("type") type: String?,
        @Query("location") location: String?,
        @Query("radius") radius: Double?
    ): Call<PlacesSummaryResponse>

    @GET("details/json")
    fun getRestaurant(
        @Query("key") key: String,
        @Query("place_id") placeId: String
    ): Call<PlaceDetailsResponse>
}