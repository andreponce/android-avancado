package com.ponce.cesarschool.braziliancities.ui.activity

import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ponce.cesarschool.braziliancities.R
import com.ponce.cesarschool.braziliancities.databinding.ActivityCityDetailBinding
import com.ponce.cesarschool.braziliancities.databinding.ActivityMapsBinding
import com.ponce.cesarschool.braziliancities.model.City
import java.io.File

class ActivityMaps : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding : ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var citiesList :ArrayList<City>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        citiesList = intent.getParcelableArrayListExtra<City>("cities") as ArrayList<City>

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map

        citiesList.forEach { city ->
            val coder = Geocoder(this)
            var fromLocationName = coder.getFromLocationName("${city.name} City", 1)
            while (fromLocationName.size==0) {
                fromLocationName = coder.getFromLocationName("${city.name} City", 1);
            }
            val location = fromLocationName[0]
            val point = LatLng(location.latitude, location.longitude)
            mMap.addMarker(
                MarkerOptions().position(point)
                    .title(city.name)
            )
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-14.235004, -51.925282), 3.5f))
    }
}