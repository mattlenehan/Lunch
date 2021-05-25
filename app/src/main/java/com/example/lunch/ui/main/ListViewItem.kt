package com.example.lunch.ui.main

sealed class ListViewItem(
    open val placeId: String,
    open val type: ListViewItemType
) : ViewItem<ListViewItem> {
    override fun compareTo(other: ListViewItem): Int {
        return this.placeId.compareTo(other.placeId)
    }

    override fun areContentsTheSame(other: ListViewItem) =
        this.placeId == other.placeId

    override fun areItemsTheSame(other: ListViewItem) =
        type == other.type && placeId == other.placeId

    data class RestaurantViewItem(
        override val placeId: String,
        val name: String,
        val image: String?,
        val isFavorite: Boolean
    ) : ListViewItem(
        placeId = placeId,
        type = ListViewItemType.RESTAURANT
    )
}

enum class ListViewItemType {
    RESTAURANT
}