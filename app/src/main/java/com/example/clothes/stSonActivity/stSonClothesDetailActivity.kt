package com.example.clothes.stSonActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.clothes.R
import com.example.clothes.stFragmentClothes
import com.squareup.picasso.Picasso

class stSonClothesDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_st_son_clothes_detail)
        val clothesDetail = intent.getParcelableExtra<stFragmentClothes>("clothesDetail")
        val clothes_pic : ImageView = findViewById(R.id.clothes_pic)
        Picasso
            .with(this)
            .load(clothesDetail?.imageUrl)
            .placeholder(R.drawable.loading)
            .error(R.drawable.logo)
            .into(clothes_pic);
        val clothes_name : TextView = findViewById(R.id.clothes_name)
        clothes_name.text = clothesDetail?.name
    }
}