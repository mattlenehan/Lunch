package com.example.lunch.ui.main

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedListAdapterCallback

open class AdapterCallback<T: ViewItem<T>>(adapter: RecyclerView.Adapter<*>): SortedListAdapterCallback<T>(adapter) {
    override fun compare(o1: T, o2: T) =
        o1.compareTo(o2)

    override fun areContentsTheSame(oldItem: T, newItem: T) =
        oldItem.areContentsTheSame(newItem)

    override fun areItemsTheSame(item1: T, item2: T) =
        item1.areItemsTheSame(item2)
}