package com.example.lunch.data

import com.example.lunch.ui.main.ListViewItem

data class Restaurant(
    val placeId: String,
    val name: String,
    val type: String?,
    val photoRef: String?,
    var isFavorite: Boolean = false
)

fun Restaurant.toRestaurantViewItem(): ListViewItem.RestaurantViewItem {
    return ListViewItem.RestaurantViewItem(
        placeId,
        name,
        photoRef,
        isFavorite
    )
}