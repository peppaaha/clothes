package com.example.clothes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideActionBar()
        initNavigation()
  //      requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    private fun initNavigation() {
        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)
        val configuration = AppBarConfiguration(setOf(R.id.action_clothes, R.id.action_home))
        setupActionBarWithNavController(navController, configuration)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun hideActionBar() {
        val actionBar = supportActionBar
        actionBar?.hide()
    }


}

