package com.example.clothes.ndSonActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clothes.R

class ndSonAboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nd_son_about)
        hideActionBar()
    }

    private fun hideActionBar() {
        val actionBar = supportActionBar
        actionBar?.hide()
    }
}