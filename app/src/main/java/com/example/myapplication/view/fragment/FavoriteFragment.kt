package com.example.myapplication.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.FavouritAdapter
import com.example.myapplication.databinding.FragmentFavoriteBinding
import com.example.myapplication.model.Hourly
import com.example.myapplication.model.Model
import com.example.myapplication.viewmodel.FavoriteViewModel
import com.example.myapplication.viewmodel.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favAdapter : FavouritAdapter
    private lateinit var weathetViewModel: WeatherViewModel
    private lateinit var viewModel: FavoriteViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)
        init()

        return binding.root
    }
    private fun init() {
        weathetViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        binding.favRecycle.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        favAdapter = FavouritAdapter()
       // favouritAdapter.setOnItemClickListener(this)
        binding.btnFab.setOnClickListener(View.OnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_favoriteFragment_to_mapsFragment)
            showFavItem()

        })
    }
  /*  fun viewFavItem(latitude: String, longitude: String) {
        weathetViewModel.fetchweather(latitude, longitude).observe(viewLifecycleOwner, Observer {

            )}

    }*/
    fun showFavItem() {
        viewModel.favoriteData.observe(viewLifecycleOwner, Observer {
            it?.let {
                var listFav: List<Model> = it
                favAdapter.setData(it, requireContext())
                binding.favRecycle.adapter = favAdapter

            }
        })
    }
}