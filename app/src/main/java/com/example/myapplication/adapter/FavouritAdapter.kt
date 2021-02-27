package com.example.myapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.model.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.ArrayList

class FavouritAdapter  : RecyclerView.Adapter<FavouritAdapter.ViewHolder>(){
    private lateinit var data  : List<Model>
    private lateinit var context: Context
    private lateinit var onItemClickListLener: OnItemClickListener
    init {
       data = ArrayList<Model>()
    }
    fun setData (data : List<Model>, context: Context){
        this.data = data
        this.context = context
    }
    inner class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val textCity = itemView.findViewById(R.id.txt_city) as TextView
        val textTemp = itemView.findViewById(R.id.txt_temperature) as TextView
        val icon = itemView.findViewById(R.id.fav_icon) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.favourit_item,parent,false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = data.get(position)
        holder.textCity.text =model.timezone
        holder.textTemp.text=model.current.temp.toString()
        context?.let {
            Glide.with(it).load(RetrofitInstance.getImage(model.current.weather[0].icon)).into(holder.icon)
        }
    }
    interface OnItemClickListener {
        fun onClick(position: Int)
    }
    fun setOnItemClickListener(onItemClickListLener: OnItemClickListener) {
        this.onItemClickListLener = onItemClickListLener
    }
}
