package com.example.lunch.adapter

interface AdapterListener {
    fun onRestaurantClick(id: String)
    fun onFavoriteClick(id: String)
}