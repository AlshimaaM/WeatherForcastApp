package com.example.myapplication.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.myapplication.R
import com.example.myapplication.util.ContextUtils
import java.util.*
/*
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home,
            R.id.navigation_fav,
            R.id.navigation_alert,
            R.id.navigation_settings
        ))
        navView.setupWithNavController(navController)
    }
*//*

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAG", "LOCATION_PERMISSION_REQUEST_CODE111111 $requestCode")
        val fragment = supportFragmentManager.findFragmentById(R.id.navigation_home)
        fragment!!.onActivityResult(requestCode, resultCode, data)
    }
*//*


}*/
class MainActivity : AppCompatActivity()  {
    private lateinit var bottom_nav: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottom_nav = findViewById(R.id.nav_view)
        bottom_nav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController,null)    }

   /* override fun attachBaseContext(newBase: Context?) {

        val sp = PreferenceManager.getDefaultSharedPreferences(newBase)
        val lang = sp.getString("LANGUAGE_SYSTEM", Locale.getDefault().language)
        val context = ContextUtils.updateLocalization(newBase!!, Locale(lang))
        super.attachBaseContext(newBase)
    }*/
}
