package com.example.clothes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.clothes.stSonActivity.stSonClothesDetailActivity
import kotlinx.android.synthetic.main.st_fragment.*
import java.util.*
import kotlin.collections.ArrayList


class stFragment : BaseFragment() {

/*    val url3 = " https://api.caiyunapp.com/v2.5/C4JPhPDPmukH7xBe/"
    val url4 = ","
    val url5 = "/realtime.jsonp?callback=MYCALLBACK"
    var lng: String? = null
    var lat: String? = null

    companion object {
        fun newInstance() = stFragment()
    }
*/
    private val clothesList = ArrayList<stFragmentClothes>()

    private lateinit var viewModel: StViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val newView : View = inflater.inflate(R.layout.st_fragment, container, false)
        val prefs_weather = context?.getSharedPreferences("temp_weather", Context.MODE_PRIVATE)
        Log.d("stFragment", "temperature is ${prefs_weather?.getString("temperature", null)} before in onCreateView")
        viewModel = ViewModelProviders.of(this).get(StViewModel::class.java)
        initClothesListRecyclerView(newView)
        initCalendar(newView)
        createLiveDataObserver(newView)
        getWeather(newView)
        Log.d("stFragment", "temperature is ${prefs_weather?.getString("temperature", null)} after in onCreateView")
/*        val bundle = arguments
        val city = bundle!!.getString("city")
        val urla = url3 + lng + url4 + lat + url5
        loadData(urla)
*/
        return newView
    }


    /*    override fun onSuccess(result: String?) {
            //解析
            parseShowData(result)
        }

        private fun parseShowData(result: String?) {
            val weatherBean = Gson().fromJson(result, TrueWeatherBean::class.java)
            //be continue...
        }
    */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StViewModel::class.java)
        // TODO: Use the ViewModel
        locate.setOnClickListener {
            val intent = Intent(activity, SearchCityActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("stFragment", "on Detach")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("stFragment", "returned data is aha")
        when (requestCode) {
            1 ->  {
                view?.let { getWeather(it) }
            }
        }
    }


    private fun createLiveDataObserver(newView: View) {
        viewModel.weatherReturnToFragment.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            setWeatherDetailTextView(newView, it.getStringExtra("temperature"),
                it.getStringExtra("weather"), it.getStringExtra("humidity"))
            val editor = context?.getSharedPreferences("temp_weather", Context.MODE_PRIVATE)?.edit()
            Log.d("stFragment", "saving weather")
            editor?.putString("temperature", it.getStringExtra("temperature"))
            editor?.putString("weather", it.getStringExtra("weather"))
            editor?.putString("humidity", it.getStringExtra("humidity"))
            editor?.apply()
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
            prefs_location?.edit()?.apply {
                putFloat("lng", 116.512885F)
                putFloat("lat", 39.847469F)
                putString("city", "北京市")
                apply()
            }
            val intent = Intent(activity, SearchCityActivity::class.java)
            startActivityForResult(intent, 1)
        } else {
            val savedLng = prefs_location?.getFloat("lng", 0.0F)  // 经度
            val savedLat = prefs_location?.getFloat("lat", 0.0F)  // 纬度
            val savedCity = prefs_location?.getString("city", null)
            setCityTextView(newView, savedCity)
            val prefs_weather = context?.getSharedPreferences("temp_weather", Context.MODE_PRIVATE)
            //加载存储的天气信息以免多次联网
            Log.d("stFragment", "temperature is ${prefs_weather?.getString("temperature", null)} before")
            if(prefs_weather?.getString("temperature", null) != null) {
                Log.d("stFragment", "recovering saved weather")
                setWeatherDetailTextView(newView,
                    prefs_weather?.getString("temperature", null),
                    prefs_weather?.getString("weather", null),
                    prefs_weather?.getString("humidity", null))
                Log.d("stFragment", "temperature is ${prefs_weather?.getString("temperature", null)} after")
            } else {
                getWeatherByLngAndLat(savedLng.toDouble(), savedLat.toDouble())
            }
        }
    }

    private fun setCityTextView(newView: View, city: String?) {
        val locateTextView : TextView = newView.findViewById(R.id.locate)
        locateTextView.text = city
    }

    private fun setWeatherDetailTextView(newView: View, temperature: String?, weather: String?, humidity: String?) {
        val weatherTextView : TextView = newView.findViewById(R.id.weather)
        val temperatureTextView : TextView = newView.findViewById(R.id.temperature)
        val dryTextView : TextView = newView.findViewById(R.id.dry)
        temperatureTextView.text = temperature
        weatherTextView.text = weather
        dryTextView.text = humidity
        Log.d("stFragment", "temperature is ${temperature}")
        Log.d("stFragment", "weather is ${weather}")
        Log.d("stFragment", "humidity is ${humidity}")
    }

    private fun getWeatherByLngAndLat(lng: Double, lat: Double) {
        val url1 = "https://api.caiyunapp.com/v2.5/C4JPhPDPmukH7xBe/"
        val url2 = "/realtime.json"
        val httpUrl = "$url1$lng,$lat$url2"
        Log.d("stFragment", "ready to start viewmodel getWeatherFromOkHttp function")
        viewModel.getWeatherFromOkHttp(httpUrl)
    }

    private fun initCalendar(newView: View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1  // The first month is 0
        val date = calendar.get(Calendar.DATE)
        val dateTextView : TextView = newView.findViewById(R.id.date)
        dateTextView.text = "$year-$month-$date"
    }

    private fun initClothesListRecyclerView(newView: View) {
        initClothes()
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        val stFragmentrecyclerView : RecyclerView = newView.findViewById(R.id.stFragmentRecyclerView)
        stFragmentrecyclerView.layoutManager = layoutManager
        val adapter = stFragmentAdapter(clothesList)
        stFragmentrecyclerView.adapter = adapter
    }

    private fun initClothes() {
        repeat(15) {
            clothesList.add(stFragmentClothes("Android Background", R.drawable.ic_launcher_background))
            clothesList.add(stFragmentClothes("Logo", R.drawable.logo))
            clothesList.add(stFragmentClothes("Rain", R.drawable.rain))
            clothesList.add(stFragmentClothes("Snow", R.drawable.snow))
            clothesList.add(stFragmentClothes("Sun", R.drawable.sun))
            clothesList.add(stFragmentClothes("Wind", R.drawable.wind))
        }
    }

}

class stFragmentClothes(val name: String, val imageId: Int)

class stFragmentAdapter(val clothesList: List<stFragmentClothes>) :
    RecyclerView.Adapter<stFragmentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clothesText: TextView = view.findViewById(R.id.clothesText)
        val clothesImage : ImageView = view.findViewById(R.id.clothesImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.st_fragment_clothes, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
          //  val position = viewHolder.adapterPosition
         //   val clothes = clothesList[position]
            val intent = Intent(parent.context, stSonClothesDetailActivity::class.java)
            startActivity(parent.context, intent, null)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothes = clothesList[position]
        holder.clothesImage.setImageResource(clothes.imageId)
        holder.clothesText.text = clothes.name
    }

    override fun getItemCount(): Int = clothesList.size

}
