package com.pic_a_pup.dev.pic_a_pup.Utilities

import android.content.Context
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.github.anastr.speedviewlib.ProgressiveGauge
import com.pic_a_pup.dev.pic_a_pup.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.search_result_card.view.*

/**
 * Created by davidseverns on 3/25/18.
 */
class ResultViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView) {
    //val cardView = itemView!!.findViewById<CardView>(R.id.homefeed_cardview)
    val resultImgView = itemView!!.findViewById<ImageView>(R.id.dog_img)
    var progressGauge = itemView!!.findViewById<ProgressiveGauge>(R.id.dog_prob_gauge_card)
    var breedText = itemView!!.findViewById<TextView>(R.id.card_breed)


    fun onBindView(mContext: Context, resultImg: String, probability: Float, breed: String){
        progressGauge.speedometerColor = mContext.getColor(R.color.colorPrimary)
        progressGauge.isWithTremble = false
        progressGauge.setSpeedAt(probability.times(100))
        breedText.text = breed
        Picasso.with(mContext.applicationContext).load(resultImg).into(resultImgView)
    }
}