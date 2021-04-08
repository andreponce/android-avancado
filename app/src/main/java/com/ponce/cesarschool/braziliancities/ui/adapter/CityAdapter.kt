package com.ponce.cesarschool.braziliancities.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ponce.cesarschool.braziliancities.databinding.ItemCityBinding
import com.ponce.cesarschool.braziliancities.model.City
import com.ponce.cesarschool.braziliancities.ui.activity.CityDetailActivity
import java.io.File
import java.net.URI

class CityAdapter : ListAdapter<City, CityAdapter.ViewHolder>(CityDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = getItem(position)
        with(holder){
            val context = binding.root.context;
            binding.titleTxt.text = city.name;
            binding.priceTxt.text = "${city.days} ${city.price}";
            binding.image.setImageURI(File("${context.filesDir}/cidades", city.image).toUri())

            binding.root.setOnClickListener {
                val intent = Intent(context, CityDetailActivity::class.java)
                intent.putExtra("city", city);
                context.startActivity(intent)
            }
        }
    }

    inner class ViewHolder(public val binding: ItemCityBinding): RecyclerView.ViewHolder(binding.root) {}

    class CityDiff: DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City) = oldItem == newItem
        override fun areContentsTheSame(oldItem: City, newItem: City) = oldItem == newItem
    }
}