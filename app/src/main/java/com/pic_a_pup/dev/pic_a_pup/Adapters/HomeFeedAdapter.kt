package com.pic_a_pup.dev.pic_a_pup.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.pic_a_pup.dev.pic_a_pup.R

class HomeFeedAdapter(private val dogDataset: List<Dogs>) :
    RecyclerView.Adapter<HomeFeedAdapter.ViewHolder> (){

    inner class ViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFeedAdapter.ViewHolder{
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.homefeed_dog_item, false) as ImageView

        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView = dogDataset[position]
    }

    override fun getItemCount() = dogDataset.size
}