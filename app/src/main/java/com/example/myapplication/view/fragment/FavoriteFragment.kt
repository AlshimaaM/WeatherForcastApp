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
import com.example.myapplication.data.local.database.entity.FavouritEntity
import com.example.myapplication.databinding.FragmentFavoriteBinding
import com.example.myapplication.provider.Setting
import com.example.myapplication.util.ContextUtils
import com.example.myapplication.util.ContextUtils.Companion.settings
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
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.IO + job)

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)

        init( )
        settings(requireContext())
        if (weathetViewModel.internetAvailable(requireContext())) {
            if (latLng != null) {
                viewWeatherFav(latLng!!.latitude.toString(), latLng!!.longitude.toString())
            }else{
                dataFromDatabase()
            }
        }else {
            dataFromDatabase()
        }
        deleteItemBySwabbing()
        return binding.root
    }

    private fun init() {
        weathetViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        binding.favRecycle.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        favData =ArrayList()
        favAdapter = FavouritAdapter()
        favAdapter.setOnItemClickListener(this)
        if (favAdapter.getItemCount()>0){
            binding.emptyList.visibility=View.GONE
            binding.txtEmpty.visibility=View.GONE
        } else {
            binding.emptyList.visibility=View.VISIBLE
            binding.txtEmpty.visibility=View.VISIBLE
        }
        binding.btnFab.setOnClickListener({
            Navigation.findNavController(it).navigate(R.id.action_favoriteFragment_to_mapsFragment)

        })
    }
    fun viewWeatherFav(latitude: String, longitude: String) {
        weathetViewModel.fetchweather(latitude, longitude).observe(viewLifecycleOwner, Observer {
            var favouritDatabase = viewModel.dataInDatabase(it)
            uiScope.launch {
                viewModel.addFavoriteIntoDB(favouritDatabase, requireContext())
                withContext(Dispatchers.Main) {
                    dataFromDatabase()
                }
            }
        })
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
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            90, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val trip: FavouritEntity = favAdapter.getItem(position)!!
                openDialog(context, trip, viewHolder)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.favRecycle)
    }

    fun openDialog(context: Context?, trip: FavouritEntity?, viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition

        val builder1 = AlertDialog.Builder(context)
        builder1.setTitle("Are you sure delete trip " + trip?.city.toString() + " ? ")
        builder1.setCancelable(false)
        builder1.setPositiveButton("Ok") { dialog, which ->
           uiScope.launch {
                try {
                    viewModel.deleteFav(favAdapter.getItem(position)!!, requireContext())
                } catch (e: Exception) {
                    Log.i("Remove", "onBindViewHolder: a" + e.message)
                }
            }
        }
        builder1.setNegativeButton("CANCEL") { dialog, which ->
            if (context != null) {
                favAdapter.setData(favData, context)
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
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}