package com.example.myapplication.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.FavouritAdapter
import com.example.myapplication.data.local.database.entity.DaysEntity
import com.example.myapplication.data.local.database.entity.FavouritEntity
import com.example.myapplication.data.local.database.entity.HoursEntity
import com.example.myapplication.databinding.FragmentFavoriteBinding
import com.example.myapplication.model.Model
import com.example.myapplication.viewmodel.FavoriteViewModel
import com.example.myapplication.viewmodel.WeatherViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import java.util.*

class FavoriteFragment : Fragment(), FavouritAdapter.OnItemClickListener  {

    private var latLng: LatLng? = null
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favAdapter : FavouritAdapter
    private lateinit var favData:List<FavouritEntity>
    private lateinit var weathetViewModel: WeatherViewModel
    private lateinit var viewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latLng = it.getParcelable<LatLng>("latLng")!!
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)
        init( )
        if (Available(requireContext())) {
            if (latLng != null) {

                viewWeatherFav(latLng!!.latitude.toString(), latLng!!.longitude.toString())
            }else{
                dataFromDatabase()
            }
        } else {
            dataFromDatabase()
        }
        deleteItemBySwabbing()

        return binding.root
    }
    fun Available(context: Context): Boolean {
        var connected = false
        var connected1 = false
        var connected2 = false
        val s = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(s) as ConnectivityManager?
        val info = manager?.activeNetworkInfo
        if (info != null && info.isConnected) {
            connected = info.type == ConnectivityManager.TYPE_WIFI
            connected1 = info.type == ConnectivityManager.TYPE_MOBILE
            if (connected || connected1) {
                connected2 = true
            }
        } else {
            connected2 = false
        }
        return connected2
    }
    private fun init() {
        weathetViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        binding.favRecycle.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        favData =ArrayList()
        favAdapter = FavouritAdapter()
        favAdapter.setOnItemClickListener(this)
        binding.btnFab.setOnClickListener( {
            Navigation.findNavController(it).navigate(R.id.action_favoriteFragment_to_mapsFragment)

        })
    }
    fun viewWeatherFav(latitude: String, longitude: String) {
        weathetViewModel.fetchweather(latitude, longitude).observe(viewLifecycleOwner, Observer {
            var favouritDatabase = dataInDatabase(it)
            GlobalScope.launch {
                Dispatchers.IO
                viewModel.addFavoriteIntoDB(favouritDatabase, requireContext())
                withContext(Dispatchers.Main) {
                    dataFromDatabase()
                }
            }
        })
    }

    fun dataInDatabase(model: Model): FavouritEntity {
        val hourlyWeather = arrayListOf<HoursEntity>()
        for (hourlyItem in model.hourly) {
            hourlyWeather.add(
                    HoursEntity(
                            hourlyItem.dt.toInt(),
                            hourlyItem.temp,
                            hourlyItem.weather[0].icon
                    )
            )
        }
        val dailyWeather = arrayListOf<DaysEntity>()
        for (dailyItem in model.daily) {
            dailyWeather.add(
                    DaysEntity(
                            dailyItem.dt,
                            dailyItem.temp.min,
                            dailyItem.temp.max,
                            dailyItem.weather[0].icon,
                            dailyItem.sunrise,
                            dailyItem.weather[0].description
                    )
            )
        }
        val database1 = FavouritEntity(
                model.current.dt,
                model.current.temp,
                model.current.pressure,
                model.current.humidity,
                model.current.clouds,
                model.current.wind_speed,
                model.current.weather[0].icon,
                model.current.weather[0].description,
                model.timezone,
                hourlyWeather,
                dailyWeather
        )
        return database1
    }
    fun dataFromDatabase() {
        viewModel.getFavoriteFromDB(requireContext()).observe(viewLifecycleOwner, Observer {
            it?.let {
                favData = it
                favAdapter.setData(it, requireContext())
                binding.favRecycle.adapter = favAdapter

            }
        })
    }
    private fun deleteItemBySwabbing() {
        // Delete subject by swabbing item left and right
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(90, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val trip: FavouritEntity = favAdapter.getItem(position)!!
                openDialog(context, trip,viewHolder)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.favRecycle)
    }


    fun openDialog(context: Context?, trip: FavouritEntity?,viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition

        val builder1 = AlertDialog.Builder(context)
        builder1.setTitle("Are you sure delete trip " + trip?.city.toString() + " ? ")
        builder1.setCancelable(false)
        builder1.setPositiveButton("Ok") { dialog, which ->
            CoroutineScope(  Dispatchers.IO).launch {
                try {
                    viewModel.deleteFav(favAdapter.getItem(position)!! , requireContext())
                } catch (e: Exception) {
                    Log.i("Remove", "onBindViewHolder: a" + e.message)
                }
            }
            //adapter.setTrips(tripList);
        }
        builder1.setNegativeButton("CANCEL") { dialog, which ->
            if (context != null) {
                favAdapter.setData(favData,context)
            }
            dialog.dismiss()
        }
        builder1.create()
        builder1.show()
    }

    override fun onClick(position: Int) {
        val favoriteItem = bundleOf("favoriteItem" to favData.get(position))
        findNavController().navigate(R.id.action_favouritFragment_to_detailsFragment, favoriteItem)
    }
}