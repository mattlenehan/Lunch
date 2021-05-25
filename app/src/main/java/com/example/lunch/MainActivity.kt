package com.example.lunch

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lunch.adapter.AdapterListener
import com.example.lunch.adapter.RestaurantAdapter
import com.example.lunch.ui.main.MainViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_activity.*

private const val LOCATION_PERMISSION_CODE = 102
private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener, AdapterListener {

    private lateinit var viewModel : MainViewModel

    private var menu: Menu? = null

    private var showFavorites: Boolean = false

    private val restaurantAdapter by lazy {
        RestaurantAdapter(this)
    }

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        recycler.apply {
            adapter = restaurantAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.items().observe(this, { list ->
            restaurantAdapter.accept(list?.toList() ?: emptyList())
        })

        viewModel.dialogLiveData.observe(this, { place ->
            AlertDialog.Builder(this)
                .setTitle("This is \"${place.name}\"")
                .setMessage("located at\n ${place.address}")
                .setCancelable(true)
                .setPositiveButton("ok"
                ) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        })

        checkAndRequestPermissionsForLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu;
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.view_favorites -> {
                showFavorites = showFavorites.not()
                viewModel.toggleFavorites(showFavorites)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
            ) {
                loadLocation()
            } else {
                Toast.makeText(this, "No Permission", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkAndRequestPermissionsForLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else {
            loadLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                // request the location
                fusedLocationProviderClient.requestLocationUpdates(
                    LocationRequest.create(),
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)

                            locationResult.locations.lastOrNull().let { location ->
                                if (location == null) {
                                    Log.d(TAG, "Location load fail")
                                    false
                                } else {
                                    viewModel.onLocationLoaded(LatLng(location.latitude, location.longitude))
                                    true
                                }
                            }
                            fusedLocationProviderClient.removeLocationUpdates(this)
                        }
                    },
                    null
                )
            } else {
                viewModel.onLocationLoaded(LatLng(location.latitude, location.longitude))
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        viewModel.onSearchQueryChanged(query ?: "")
        return true
    }

    companion object {
        const val FETCH_MORE_THRESHOLD = 5
    }

    override fun onRestaurantClick(id: String) {
        viewModel.showDetails(id)
    }

    override fun onFavoriteClick(id: String) {
        viewModel.updateFavoriteState(id)
        restaurantAdapter.notifyDataSetChanged()
    }

}