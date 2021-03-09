package com.example.myapplication.view.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
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
import com.example.myapplication.provider.Setting
import com.example.myapplication.util.ContextUtils
import com.example.myapplication.util.ContextUtils.Companion.setLocale
import com.example.myapplication.util.ContextUtils.Companion.settings

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
            activity?.let {
                ContextUtils.setLocale(it, Setting.languageSystem)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_details, container, false)
        binding.hoursRecyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.daysRecyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
       // shared= SharedPrefrence(requireContext())
        hour = HoursAdapter()
        daily = DayAdapter()
        settings(requireContext())
        setLocale(requireActivity(),Setting.languageSystem)
        favoriteItem?.let{
            var sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context)
            if (sharedPreferences.getString("UNIT_SYSTEM","metric").equals("metric")) {
                binding.tempreture.text = favoriteItem!!.temp.toString() + "°C"
                binding.wind.text = favoriteItem!!.wind_speed.toString()+ " " +"m/s"
            } else if (sharedPreferences.getString("UNIT_SYSTEM","").equals("standard")) {
                binding.tempreture.text =favoriteItem!!.temp.toString() + "°K"
                binding.wind.text = favoriteItem!!.wind_speed.toString()+ " " +"m/s"
            } else {
                binding.tempreture.text =favoriteItem!!.temp.toString() + "°F"
                binding.wind.text = favoriteItem!!.wind_speed.toString()+ " " +"m/h"
            }
            binding.pressure.text = favoriteItem!!.pressure.toString()+ " hpa"
            binding.dateHome.text = "${RetrofitInstance.dateNow}"
            binding.humidity.text =favoriteItem!!.humidity.toString() + "%"
            binding.cloud.text = favoriteItem!!.clouds.toString()
            binding.cityName.text = favoriteItem!!.city
            binding.discription.text = favoriteItem!!.desc
            binding.maxTep.text=favoriteItem!!.dailyWeather[0].maxTemp.toString()
            binding.minTep.text=favoriteItem!!.dailyWeather[0].minTemp.toString()

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