package com.example.clothes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.clothes.stSonActivity.Realtime
import com.example.clothes.stSonActivity.stSonClothesDetailActivity
import com.squareup.picasso.Picasso
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.st_fragment.*
import kotlinx.android.synthetic.main.st_fragment_clothes.*
import kotlinx.android.synthetic.main.st_fragment_clothes.view.*
import java.util.*
import kotlin.collections.ArrayList


class stFragment : BaseFragment() {

    private var clothesList = ArrayList<stFragmentClothes>()

    private lateinit var viewModel: StViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val newView : View = inflater.inflate(R.layout.st_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(StViewModel::class.java)
        initCalendar(newView)
        createLiveDataObserver(newView)
        getWeather(newView)

        return newView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StViewModel::class.java)
        // TODO: Use the ViewModel
        locate.setOnClickListener {
            val intent = Intent(activity, SearchCityActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("stFragment", "returned data is aha")
        when (requestCode) {
            1 -> {
                clothesList.clear()
                viewModel.tempArray.clear()
                view?.let { getWeather(it) }
            }
        }
    }


    private fun createLiveDataObserver(newView: View) {
        val editor = context?.getSharedPreferences("temp_weather", Context.MODE_PRIVATE)?.edit()
        viewModel.weatherReturnToFragment.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val realtime = it.getParcelableExtra<Realtime>("realtime")
            val weatherString = it.getParcelableExtra<WeatherString>("weatherString")
            setWeatherDetailTextView(
                newView, weatherString?.temperature,
                weatherString?.weather, weatherString?.humidity
            )
            Log.d("stFragment", "saving weather")
            //临时存储 简单一些
            editor?.putString("temperature", weatherString?.temperature)
            editor?.putString("weather", weatherString?.weather)
            editor?.putString("humidity", weatherString?.humidity)
            editor?.apply()
            viewModel.calcClothesLevel(realtime!!)
        })

        viewModel.clothesLevelReturnToFragment.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                Log.d("stFragment", "level is $it return from viewModel")
                setLevelTextView(newView, it)
                editor?.putInt("level", it)
                editor?.apply()
                initClothesListRecyclerView(newView, it)
            })
    }


    //1.判断程序是否是第一次运行（以后可以移到MainActivity里）
    //2.联网获取天气信息
    private fun getWeather(newView: View) {
        val prefs_location = context?.getSharedPreferences("user_location", Context.MODE_PRIVATE)
        Log.d("stFragment", "lng is ${prefs_location?.getFloat("lng", -100000F)}")
        Log.d("stFragment", "lat is ${prefs_location?.getFloat("lat", -100000F)}")
        Log.d("stFragment", "city is ${prefs_location?.getString("city", null)}")
        if(prefs_location?.getString("city", null) == null) {
            Toast.makeText(context, "程序第一次运行，请先输入您的城市", Toast.LENGTH_SHORT).show()
            //默认城市
            prefs_location?.edit()?.apply {
                putFloat("lng", 116.512885F)
                putFloat("lat", 39.847469F)
                putString("city", "北京市")
                apply()
            }
            val intent = Intent(activity, SearchCityActivity::class.java)
            startActivityForResult(intent, 1)
        } else {
            val savedLng = prefs_location.getFloat("lng", 0.0F)  // 经度
            val savedLat = prefs_location.getFloat("lat", 0.0F)  // 纬度
            val savedCity = prefs_location.getString("city", null)
            setCityTextView(newView, savedCity)
            val prefs_weather = context?.getSharedPreferences("temp_weather", Context.MODE_PRIVATE)
            //加载存储的天气信息以免多次联网
            Log.d(
                "stFragment",
                "temperature is ${prefs_weather?.getString("temperature", null)} before"
            )
            if(prefs_weather?.getString("temperature", null) != null) {
                Log.d("stFragment", "recovering saved weather")
                setWeatherDetailTextView(
                    newView,
                    prefs_weather.getString("temperature", null),
                    prefs_weather.getString("weather", null),
                    prefs_weather.getString("humidity", null)
                )
                val level = prefs_weather.getInt("level", -100)
                setLevelTextView(newView, level)
                Log.d(
                    "stFragment",
                    "temperature is ${prefs_weather.getString("temperature", null)} after"
                )
                initClothesListRecyclerView(newView, level)
            } else {
                viewModel.getWeatherByLngAndLat(savedLng.toDouble(), savedLat.toDouble())
            }
        }
    }

    private fun setCityTextView(newView: View, city: String?) {
        val locateTextView : TextView = newView.findViewById(R.id.locate)
        locateTextView.text = city
    }

    private fun setLevelTextView(newView: View, level: Int) {
        val npc2 : TextView = newView.findViewById(R.id.npc2)
        npc2.text = "今日推荐(${level}级)"
    }

    private fun setWeatherDetailTextView(
        newView: View,
        temperature: String?,
        weather: String?,
        humidity: String?
    ) {
        val weatherTextView : TextView = newView.findViewById(R.id.weather)
        val temperatureTextView : TextView = newView.findViewById(R.id.temperature)
        val dryTextView : TextView = newView.findViewById(R.id.dry)
        temperatureTextView.text = temperature
        weatherTextView.text = weather
        dryTextView.text = humidity
        Log.d("stFragment", "temperature is ${temperature}")
        Log.d("stFragment", "weather is ${weather}")
        Log.d("stFragment", "humidity is ${humidity}")
        val backgroundLayout : ConstraintLayout? = activity?.findViewById(R.id.background)
        backgroundLayout?.setBackgroundResource(
            when (weather) {
                "晴" -> R.drawable.sun
                "多云", "阴" -> R.drawable.cloud
                "小雨", "中雨", "大雨", "暴雨" -> R.drawable.rain
                "小雪", "中雪", "大雪", "暴雪" -> R.drawable.snow
                "大风" -> R.drawable.wind
                else -> R.drawable.sun
            }
        )
    }

    private fun initCalendar(newView: View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1  // The first month is 0
        val date = calendar.get(Calendar.DATE)
        val dateTextView: TextView = newView.findViewById(R.id.date)
        dateTextView.text = "$year-$month-$date"
    }

    private fun initClothesListRecyclerView(newView: View, level: Int) {
        initClothes(level)
        Log.d("stFragment", "ready to observe files")
        viewModel.filesListReturnToFragment.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                Log.d("stFragment", "file list return to fragment now")
                val stFragmentrecyclerView: RecyclerView =
                    newView.findViewById(R.id.stFragmentRecyclerView)
                val layoutManager =
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                stFragmentrecyclerView.layoutManager = layoutManager
                clothesList.addAll(it)
                val adapter = stFragmentAdapter(it, this)
                stFragmentrecyclerView.adapter = adapter
            })
    }

    private fun initClothes(level: Int) {
        val urlRoot = "http://8.136.214.13/images/$level"
        viewModel.getRemoteServerFilesListFromOkHttp(urlRoot)
    }
}

//以下为 RecyclerView 部分
@Parcelize
class stFragmentClothes(val name: String, val imageUrl: String): Parcelable

class stFragmentAdapter(val clothesList: List<stFragmentClothes>, val fragment : Fragment) :
    RecyclerView.Adapter<stFragmentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clothesText: TextView = view.findViewById(R.id.clothesText)
        val clothesImage : ImageView = view.findViewById(R.id.clothesImage)
        val feelImage : ImageView = view.findViewById(R.id.feelImage)
    }

    private lateinit var mRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.st_fragment_clothes, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val clothes = clothesList[position]
            val intent = Intent(parent.context, stSonClothesDetailActivity::class.java)
            intent.putExtra("clothesDetail", clothes)
            startActivity(parent.context, intent, null)
        }
        viewHolder.feelImage.setOnClickListener{
            val position = viewHolder.adapterPosition
            val clothes = clothesList[position]
            val popup = PopupMenu(parent.context,view.feelImage)
            //Inflating the Popup using xml file
            popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
            val viewModel = ViewModelProviders.of(fragment).get(StViewModel::class.java)
            popup.setOnMenuItemClickListener { item ->
                when (item!!.itemId) {
                    R.id.feeling1 -> {
                        viewModel.handleFeedback(1)
                        Toast.makeText(parent.context, "${item.title}已反馈", Toast.LENGTH_SHORT).show()
                        Log.d("stFragment", "too cold feedback")
                    }
                    R.id.feeling2 -> {
                        viewModel.handleFeedback(-1)
                        Toast.makeText(parent.context, "${item.title}已反馈", Toast.LENGTH_SHORT).show()
                        Log.d("stFragment", "too hot feedback")
                    }
                    R.id.feeling3 -> {
                        Toast.makeText(parent.context, item.title, Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }

            popup.show()
        }

            return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothes = clothesList[position]
        Log.d("stFragment", "url is ${clothes.imageUrl}")
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
