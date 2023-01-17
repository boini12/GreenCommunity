package org.wit.greencommunity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import org.wit.greencommunity.R
import org.wit.greencommunity.databinding.CardAdBinding
import org.wit.greencommunity.models.AdModel

interface AdListener {
    fun onAdClick(ad: AdModel)
}

class AdAdapter(private var ads: ArrayList<AdModel>, private val listener: AdListener) :
    RecyclerView.Adapter<AdAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardAdBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val ad = ads[position]

        holder.ad_title.text = ad.title
        holder.ad_price.text = ad.price.toString()
        holder.ad_img.setImageURI(ad.adImg?.toUri())

        holder.bind(ad, listener)
    }

    override fun getItemCount(): Int = ads.size

    class MainHolder(private val binding: CardAdBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val ad_title: TextView = itemView.findViewById<TextView>(R.id.adTitle)
        val ad_price: TextView = itemView.findViewById<TextView>(R.id.adPrice)
        val ad_img : ImageView = itemView.findViewById<ImageView>(R.id.adCardImg)

        fun bind(ad: AdModel, listener: AdListener) {

            binding.root.setOnClickListener { listener.onAdClick(ad)}
        }
    }
}