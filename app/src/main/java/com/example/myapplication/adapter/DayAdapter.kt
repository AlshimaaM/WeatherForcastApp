package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.local.database.entity.DaysEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.model.Daily
import com.example.myapplication.util.Constant
import java.util.*

class DayAdapter : RecyclerView.Adapter<DayAdapter.ViewHolder>() {
    private lateinit var days  : List<DaysEntity>
    private lateinit var context :Context


    init {
        days = ArrayList<DaysEntity>()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.day_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return days.size
    }
    fun fetchData (days: List<DaysEntity>, context: Context){
        this.days = days
        this.context=context

    }
    override fun onBindViewHolder(holder: DayAdapter.ViewHolder, position: Int) {
          holder.bind(days[position],context)
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val max_temp = itemView.findViewById(R.id.day_max_temp) as TextView
        val icon = itemView.findViewById(R.id.day_icon) as ImageView
        val min_temp = itemView.findViewById(R.id.day_min_temp) as TextView
        val day_name = itemView.findViewById(R.id.day_name) as TextView

        fun bind(days: DaysEntity, context: Context) {
            max_temp.text = days.maxTemp.toString()
            min_temp.text=days.minTemp.toString()
            var temp=days.date
            day_name.text= Constant.convertLongToDay(temp)

            context?.let {
                Glide.with(it).load(RetrofitInstance.getImage(days.icon)).into(icon)
            }
        }
    }

}