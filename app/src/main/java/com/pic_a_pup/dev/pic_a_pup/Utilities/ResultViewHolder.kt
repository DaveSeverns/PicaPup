package com.pic_a_pup.dev.pic_a_pup.Utilities

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.pic_a_pup.dev.pic_a_pup.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.search_result_card.view.*

/**
 * Created by davidseverns on 3/25/18.
 */
class ResultViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView) {
    val cardView = itemView!!.findViewById<CardView>(R.id.homefeed_cardview)

    fun onBindView(mContext: Context, resultImg: String){
        val resultImgView = itemView!!.findViewById<ImageView>(R.id.homefeed_dog_image)

        Picasso.with(mContext.applicationContext).load(resultImg).into(resultImgView)
    }
}