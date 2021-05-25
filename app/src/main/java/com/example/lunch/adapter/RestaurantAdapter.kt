package com.example.lunch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.example.lunch.R
import com.example.lunch.ui.main.AdapterCallback
import com.example.lunch.ui.main.ListViewItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.place_view_item.view.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class RestaurantAdapter(private val listener: AdapterListener): RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    private val restaurantList = SortedList(ListViewItem::class.java, AdapterCallback(this))

    fun accept(newItems: List<ListViewItem>) =
        restaurantList.replaceAll(newItems)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_view_item, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val item = restaurantList.get(position)
        holder.bind(item as ListViewItem.RestaurantViewItem, listener)
    }

    override fun getItemCount(): Int {
        return restaurantList.size()
    }

    class RestaurantViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(restaurant: ListViewItem.RestaurantViewItem, listener: AdapterListener) {
            itemView.name.text = restaurant.name
            if (restaurant.image?.isNotBlank() == true) {
                Picasso
                    .get()
                    .load(
                        "https://maps.googleapis.com/maps/api/place/photo".toHttpUrlOrNull()
                            ?.newBuilder()
                            ?.addQueryParameter("key", "AIzaSyDQSd210wKX_7cz9MELkxhaEOUhFP0AkSk")
                            ?.addQueryParameter("maxwidth", "250")
                            ?.addQueryParameter("photoreference", restaurant.image)
                            .toString())
                    .into(itemView.image)
            }
            itemView.rootView.setOnClickListener {
                listener.onRestaurantClick(restaurant.placeId)
            }

            if (restaurant.isFavorite) {
                itemView.favorite.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_star_filled))
            } else {
                itemView.favorite.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_star_border))
            }

            itemView.favorite.setOnClickListener {
                listener.onFavoriteClick(restaurant.placeId)
            }
        }
    }
}