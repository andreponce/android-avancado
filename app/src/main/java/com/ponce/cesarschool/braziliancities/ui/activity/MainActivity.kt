package com.ponce.cesarschool.braziliancities.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ponce.cesarschool.braziliancities.R
import com.ponce.cesarschool.braziliancities.databinding.ActivityMainBinding
import com.ponce.cesarschool.braziliancities.model.City
import com.ponce.cesarschool.braziliancities.service.DownloadService
import com.ponce.cesarschool.braziliancities.ui.adapter.CityAdapter
import org.json.JSONObject
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding :ActivityMainBinding
    private val cityAdapter by lazy { CityAdapter() }
    private lateinit var citiesList :ArrayList<City>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI();
    }

    private fun initUI(){
        binding.rvCities.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cityAdapter
        }

        val citiesFile = File(filesDir, "cidades")
        val json = File(filesDir, "pacotes.json")

        if(citiesFile.exists() && json.exists()){
            binding.downloadBt.visibility = View.GONE;
            binding.rvCities.visibility = View.VISIBLE

            val jsonString = json.bufferedReader().use { it.readText() }
            val data = JSONObject(jsonString)
            val citiesData = data.getJSONArray("pacotes")
            citiesList = ArrayList<City>()
            for (i in 0 until citiesData.length()) {
                val item = citiesData.getJSONObject(i)
                citiesList.add(
                    City(
                        item.getString("local"),
                        item.getString("imagem"),
                        item.getString("dias"),
                        item.getString("preco")
                    )
                )

            }
            cityAdapter.submitList(citiesList)
        }else{
            binding.downloadBt.visibility = View.VISIBLE;
            binding.rvCities.visibility = View.GONE
            binding.downloadBt.setOnClickListener {
                downloadFiles();
            }
        }
    }

    private fun downloadFiles(){
        if(!DownloadService.isRunning(this)){
            val urlsList  = ArrayList<String>()
            urlsList.add("https://github.com/haldny/imagens/raw/main/cidades.zip")
            urlsList.add("https://raw.githubusercontent.com/haldny/imagens/main/pacotes.json")

            Toast.makeText(this, "Baixando arquivos", Toast.LENGTH_SHORT).show();

            val intent = Intent(this, DownloadService::class.java)
            intent.putExtra("urls", urlsList);
            DownloadService.enqueueWork(this, intent)
        }else{
            Toast.makeText(this, "Os arquivos estão sendo baixados", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.maps -> {
            val intent = Intent(this, ActivityMaps::class.java)
            intent.putExtra("cities", citiesList)
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(this::citiesList.isInitialized) menuInflater.inflate(R.menu.menu_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        running = true
    }

    override fun onPause() {
        super.onPause()
        running = false
    }

    companion object {
        const val TAG :String = "MainActivity"
        var running :Boolean = false
    }
}