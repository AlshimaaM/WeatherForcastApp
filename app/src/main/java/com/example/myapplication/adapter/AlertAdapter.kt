package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.local.database.entity.AlertEntity

import java.util.*

class AlertAdapter (val context: Context) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    private  var alertList  : MutableList<AlertEntity>


    init {
        alertList = ArrayList<AlertEntity>()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.alert_item,parent,false)
        return ViewHolder(v)
    }
    override fun getItemCount(): Int {
        return alertList.size
    }
    fun fetchData (alertList : MutableList<AlertEntity>, context: Context){
        this.alertList = alertList
    }
    override fun onBindViewHolder(holder: AlertAdapter.ViewHolder, position: Int) {
        holder.time.text = alertList[position].start
    }
    /*
  SetUp Of Delete Item
   */
    fun getItemByVH(viewHolder: RecyclerView.ViewHolder): AlertEntity {
        return alertList.get(viewHolder.adapterPosition)
    }
    fun removeAlertItem(viewHolder: RecyclerView.ViewHolder){
        alertList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val time = itemView.findViewById(R.id.time) as TextView


    }
}