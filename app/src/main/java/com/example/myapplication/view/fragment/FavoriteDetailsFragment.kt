package com.example.myapplication.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.adapter.DayAdapter
import com.example.myapplication.adapter.HoursAdapter
import com.example.myapplication.data.local.database.entity.DaysEntity
import com.example.myapplication.data.local.database.entity.FavouritEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.databinding.FragmentFavoriteDetailsBinding

class FavoriteDetailsFragment : Fragment() {
    private var favoriteItem: FavouritEntity?=null
    private lateinit var daily : DayAdapter
    private lateinit var hour : HoursAdapter
    private lateinit var binding : FragmentFavoriteDetailsBinding
    private lateinit var icon : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            favoriteItem = it.getParcelable<FavouritEntity>("favoriteItem")!!

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_details, container, false)
        binding.hoursRecyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.daysRecyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        hour = HoursAdapter()
        daily = DayAdapter()
        favoriteItem?.let{
            var sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context)
            if(sharedPreferences.getString("UNIT_SYSTEM", "")=="K") {
                binding.tempreture.text = favoriteItem!!.temp.toString()+"°K"
            }
            else if(sharedPreferences.getString("UNIT_SYSTEM", "")=="C")
            {
                binding.tempreture.text = favoriteItem!!.temp.toString()+"°C"
            }
            else
            {
                binding.tempreture.text = favoriteItem!!.temp.toString()+"°F"
            }
            binding.pressure.text = favoriteItem!!.pressure.toString()
            binding.dateHome.text = "${RetrofitInstance.dateNow}"
            binding.humidity.text =favoriteItem!!.humidity.toString() + "%"
            binding.cloud.text = favoriteItem!!.clouds.toString()
            binding.cityName.text = favoriteItem!!.city
            binding.wind.text = favoriteItem!!.wind_speed.toString()
            binding.discription.text = favoriteItem!!.desc

            var list: List<HoursEntity> = it.hourlyWeather
            var listDaily: List<DaysEntity> = it.dailyWeather
            icon = it.icon
            context?.let {
                Glide.with(it).load(RetrofitInstance.getImage(icon)).into(binding.iconToday)
                hour.setData(list, it)
                binding.hoursRecyclerview.adapter = hour
                daily.fetchData(listDaily, it)
                binding.daysRecyclerview.adapter = daily
            }
        }

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FavoriteDetailsFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}