package com.dicoding.picodiploma.loginwithanimation.view.maps

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.model.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        userPreference = UserPreference.getInstance(dataStore)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        loadStoryLocations()
        getMyLocation()
    }

    private fun loadStoryLocations() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userPreference = UserPreference.getInstance(dataStore)
                val token = userPreference.getSession().first().token

                if (token.isEmpty()) {
                    runOnUiThread {
                        Toast.makeText(this@MapsActivity, "No valid token found. Please login again.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val response = ApiConfig.getInstance().getStoriesWithLocation("Bearer $token")
                if (response.isSuccessful) {
                    val stories = response.body()?.listStory ?: emptyList()

                    if (stories.isEmpty()) {
                        runOnUiThread {
                            Toast.makeText(this@MapsActivity, "No stories with location found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        addMarkersToMap(stories)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MapsActivity, "Failed to load stories: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MapsActivity, "Error loading stories: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addMarkersToMap(stories: List<ListStoryItem>) {
        runOnUiThread {
            if (stories.isEmpty()) {
                Toast.makeText(this, "No markers to display", Toast.LENGTH_SHORT).show()
                return@runOnUiThread
            }

            stories.forEach { story ->
                val lat = story.lat ?: return@forEach
                val lon = story.lon ?: return@forEach
                val latLng = LatLng(lat, lon)

                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)
                )
                boundsBuilder.include(latLng)
            }

            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                Toast.makeText(this, "Normal Map Selected", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                Toast.makeText(this, "Satellite Map Selected", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                Toast.makeText(this, "Terrain Map Selected", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                Toast.makeText(this, "Hybrid Map Selected", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}