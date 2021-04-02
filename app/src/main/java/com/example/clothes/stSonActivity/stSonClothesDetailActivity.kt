package com.example.clothes.stSonActivity

import android.app.Activity
import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.clothes.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.st_fragment_clothes.view.*
import okhttp3.*
import java.io.IOException
import java.util.regex.Pattern
import kotlin.concurrent.thread


@Parcelize
class stSonClothesDetailClothes(val name: String, val imageUrl: String): Parcelable


@Suppress("DEPRECATION")
class stSonClothesDetailActivity : AppCompatActivity() {

    var tempArray = ArrayList<stSonClothesDetailClothes>()
    val initClothesList = 1

    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.what) {
                initClothesList ->
                    initClothesListRecyclerView()
            }
        }
    }  //ToDo: 替换过时的方法

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_st_son_clothes_detail)
        hideActionBar()
        val clothesDetail = intent.getParcelableExtra<stFragmentClothes>("clothesDetail")
        val clothes_url = clothesDetail!!.imageUrl
        Log.d("stSonClothesDetail", "pre pre url is $clothes_url")
        val httpUrl = convertStringToNoPoint(clothes_url)
        Log.d("stSonClothesDetail", "pre url is $httpUrl")
        val clothes_name : TextView = findViewById(R.id.clothes_name)
        clothes_name.text = clothesDetail.name
        sendOkHttpRequest(httpUrl, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                thread {
                    try {
                        Log.d("stSonClothesDetail", "now response")
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            val gson = Gson()
                            val list = gson.fromJson(responseData, RemoteServerFilesListBean::class.java)
                            for (file in list) {
                                if (TextUtils.lastIndexOf(file.name, '.') != -1)
                                    tempArray.add(
                                        stSonClothesDetailClothes(
                                            clothesDetail.name + file.name.substring(0, TextUtils.lastIndexOf(file.name, '.')
                                            )
                                            , "$httpUrl/${file.name}"
                                        )
                                    )
                                Log.d("stSonClothesDetail", "url is $httpUrl/${file.name}")
                                Log.d("stSonClothesDetail", "name is ${clothesDetail.name + file.name.substring(0,
                                    TextUtils.lastIndexOf(file.name, '.'))}")
                            }
                            val msg = Message()
                            msg.what = initClothesList
                            handler.sendMessage(msg)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("stSonClothesDetail", "connecting internet on failure")
                e.printStackTrace()
            }
        })
    }


    private fun convertStringToNoPoint(oldString: String) : String {
        return oldString.substring(0,
            TextUtils.lastIndexOf(oldString, '.'))
    }

    private fun initClothesListRecyclerView() {
        val stSonClothesDetailRecyclerView: RecyclerView = findViewById(R.id.stSonClothesDetailRecyclerView)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        stSonClothesDetailRecyclerView.layoutManager = layoutManager
        val adapter = stSonClothesDetailAdapter(tempArray, this)
        stSonClothesDetailRecyclerView.adapter = adapter
    }

    private fun sendOkHttpRequest(address: String, callback: Callback) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(address)
            .build()
        client.newCall(request).enqueue(callback)
    }

    private fun hideActionBar() {
        val actionBar = supportActionBar
        actionBar?.hide()
    }
}





class stSonClothesDetailAdapter(val clothesList: List<stSonClothesDetailClothes>, val activity : Activity) :
    RecyclerView.Adapter<stSonClothesDetailAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clothesText: TextView = view.findViewById(R.id.clothesText)
        val clothesImage : ImageView = view.findViewById(R.id.clothesImage)
    }

    private lateinit var mRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.st_fragment_clothes_detail, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothes = clothesList[position]
        Picasso
            .with(mRecyclerView.context)
            .load(clothes.imageUrl)
            .placeholder(R.drawable.loading)
            .error(R.drawable.logo)
            .into(holder.clothesImage);
        holder.clothesText.text = clothes.name
    }

    override fun getItemCount(): Int = clothesList.size

}
