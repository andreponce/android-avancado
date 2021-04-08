package com.ponce.cesarschool.braziliancities.ui.activity

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ponce.cesarschool.braziliancities.R
import com.ponce.cesarschool.braziliancities.databinding.ActivityCityDetailBinding
import com.ponce.cesarschool.braziliancities.model.City
import java.io.File


class CityDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding : ActivityCityDetailBinding
    private lateinit var mMap: GoogleMap
    private lateinit var city: City

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        city = intent.getParcelableExtra<City>("city")!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = city.name;

        binding.nameTxt.text = city.name;
        binding.daysTxt.text = city.days;
        binding.priceTxt.text = city.price;
        binding.image.setImageURI(File("${filesDir}/cidades", city.image).toUri())

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map

//        Geocoder.isPresent();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 9f))
    }
}