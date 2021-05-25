package com.example.lunch.ui.main

import androidx.lifecycle.*
import com.example.lunch.data.Restaurant
import com.example.lunch.data.toRestaurantViewItem
import com.example.lunch.network.PlaceDetails
import com.example.lunch.network.PlaceDetailsResponse
import com.example.lunch.network.PlacesSummaryResponse
import com.example.lunch.repository.RestaurantRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val repository: RestaurantRepository
) : ViewModel() {

    val itemsLiveData = MutableLiveData<MutableSet<Restaurant>>(mutableSetOf())

    val favoritesLiveData = MutableLiveData<Boolean>(false)

    val searchQueryLiveData = MutableLiveData<String>()

    val locationLiveData = MutableLiveData<LatLng>()

    val dialogLiveData = MutableLiveData<PlaceDetails>()

    fun items() = combine(itemsLiveData, searchQueryLiveData, favoritesLiveData) { items, query, isFavorites ->
        items?.filter {
            it.name.lowercase().contains(query?.lowercase() ?: "") &&
                    if (isFavorites == true) it.isFavorite else true
        }?.map {
            it.toRestaurantViewItem()
        }?.toMutableSet()
    }.distinctUntilChanged()

    fun onSearchQueryChanged(query: String) {
        if (query.length > 3) {
            searchQueryLiveData.value = query
            loadRestaurants()
        }
    }

    fun onLocationLoaded(latLng: LatLng) {
        locationLiveData.value = latLng
        loadRestaurants()
    }

    fun toggleFavorites(showFavorites: Boolean) {
        favoritesLiveData.postValue(showFavorites)
        loadRestaurants()
    }

    fun showDetails(id: String) {
        repository.getRestaurant(
            id = id
        ).enqueue(object : Callback<PlaceDetailsResponse> {
            override fun onResponse(
                call: Call<PlaceDetailsResponse>,
                response: Response<PlaceDetailsResponse>
            ) {
                val result = response.body()?.result ?: return
                dialogLiveData.postValue(result)
            }

            override fun onFailure(call: Call<PlaceDetailsResponse>, t: Throwable) {}

        })
    }

    fun updateFavoriteState(id: String) {
        itemsLiveData.postValue(itemsLiveData.value?.map {
            if (it.placeId == id) {
                it.copy(isFavorite = it.isFavorite.not())
            } else {
                it
            }
        }?.toMutableSet())
        itemsLiveData.value = itemsLiveData.value
    }

    private fun loadRestaurants() {
        repository.getRestaurants(
            query = searchQueryLiveData.value ?: "",
            userLocation = locationLiveData.value ?: LatLng(0.0, 0.0)
        ).enqueue(object : Callback<PlacesSummaryResponse> {
            override fun onResponse(
                call: Call<PlacesSummaryResponse>,
                response: Response<PlacesSummaryResponse>
            ) {
                val result = response.body()?.results ?: return
                val restaurants = result.map {
                    Restaurant(
                        placeId = it.placeId,
                        name = it.name ?: "Unknown Name",
                        type = it.types?.first(),
                        photoRef = it.photos?.first()?.photoReference,
                        isFavorite = false
                    )
                }.toMutableSet()
                itemsLiveData.value?.addAll(restaurants)
                itemsLiveData.value = itemsLiveData.value
            }

            override fun onFailure(call: Call<PlacesSummaryResponse>, t: Throwable) {}

        })
    }

    private fun <T1, T2, T3, R> combine(
        liveData1: LiveData<T1>,
        liveData2: LiveData<T2>,
        liveData3: LiveData<T3>,
        combineFn: (value1: T1?, value2: T2?, value3: T3?) -> R
    ): LiveData<R> = MediatorLiveData<R>().apply {
        addSource(liveData1) {
            value = combineFn(it, liveData2.value, liveData3.value)
        }
        addSource(liveData2) {
            value = combineFn(liveData1.value, it, liveData3.value)
        }
        addSource(liveData3) {
            value = combineFn(liveData1.value, liveData2.value, it)
        }
    }

    fun <T1, T2, T3, R> Triple<LiveData<T1>, LiveData<T2>, LiveData<T3>>.map(
        combineFn: (value1: T1?, value2: T2?, value3: T3?) -> R
    ) = combine(first, second, third, combineFn)
}