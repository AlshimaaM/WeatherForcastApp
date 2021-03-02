package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.data.remote.RetrofitInstance
import java.util.*

class HoursAdapter () : RecyclerView.Adapter<HoursAdapter.ViewHolder>(){
    private lateinit var hours  : List<HoursEntity>
    private lateinit var context: Context
    init {
        hours = ArrayList<HoursEntity>()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.hour_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return hours.size
    }
    fun setData(hours: List<HoursEntity>, context: Context){
        this.hours = hours
        this.context = context
    }

    override fun onBindViewHolder(holder: HoursAdapter.ViewHolder, position: Int) {
        holder.bind(hours[position],context)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val card =itemView.findViewById<CardView>(R.id.card)
        val textTemp = itemView.findViewById(R.id.hour_temp) as TextView
        val textHour = itemView.findViewById(R.id.hour) as TextView
        val icon = itemView.findViewById(R.id.hour_icon) as ImageView


        fun bind(hours: HoursEntity,context: Context) {
            textTemp.text = hours.tempture.toString()
            textHour.text = hours.date.toString()
            textHour.text="${RetrofitInstance.formateTime(hours.date)}"
            if(position %2 == 0){
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_red));
            }else {
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
            }

            context?.let {
                Glide.with(it).load(RetrofitInstance.getImage(hours.icon)).into(icon)
            }
        }
    }


}
