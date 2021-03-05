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
import com.example.myapplication.data.local.database.entity.FavouritEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.data.remote.RetrofitInstance
import java.util.*

class FavouritAdapter  : RecyclerView.Adapter<FavouritAdapter.ViewHolder>(){
    private lateinit var data  : List<FavouritEntity>
    private lateinit var context: Context
    private lateinit var onItemClickListLener: OnItemClickListener
    init {
       data = ArrayList<FavouritEntity>()
    }
    fun setData(data: List<FavouritEntity>, context: Context){
        this.data = data
        this.context = context
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        init {
            itemView.setOnClickListener(this)
        }
        val textCity = itemView.findViewById(R.id.txt_city) as TextView
        val textTemp = itemView.findViewById(R.id.txt_temperature) as TextView
        val icon = itemView.findViewById(R.id.fav_icon) as ImageView
        val card =itemView.findViewById<CardView>(R.id.card_fav)

        fun bind(data: FavouritEntity, context: Context) {
            textCity.text =data.city
           textTemp.text=data.temp.toString()
            context?.let {
                Glide.with(it).load(RetrofitInstance.getImage(data.icon)).into(icon)
            }
            if(position%2==0){
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_red));
            }else {
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
            }
        }

        override fun onClick(v: View?) {
            onItemClickListLener.onClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.favourit_item, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }
    fun getItem(position: Int): FavouritEntity? {
        return data.get(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     holder.bind(data[position],context)
    }
    interface OnItemClickListener {
        fun onClick(position: Int)
    }
    fun setOnItemClickListener(onItemClickListLener: OnItemClickListener) {
        this.onItemClickListLener = onItemClickListLener
    }
}
