package com.pic_a_pup.dev.pic_a_pup

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pic_a_pup.dev.pic_a_pup.Model.Model


class DogRecyclerAdapter(val context: Context, val dogs: List<Model.Dog>): RecyclerView.Adapter<DogRecyclerAdapter.DogViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dog_list_item,parent, false)
        return DogViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dogs.count()
    }

    override fun onBindViewHolder(holder: DogViewHolder?, position: Int) {
        holder?.bindDog(dogs[position])
    }

    inner class DogViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView){
        val dogNameText = itemView?.findViewById<TextView>(R.id.dog_name_text)
        val pupCodeText = itemView?.findViewById<TextView>(R.id.pup_code_text)

        fun bindDog(dog: Model.Dog){
            dogNameText?.text = dog.dogName
            pupCodeText?.text = dog.pupCode
        }
    }
}