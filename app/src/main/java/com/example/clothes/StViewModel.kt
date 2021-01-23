package com.example.clothes

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clothes.stSonActivity.WeatherDetailsBean
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.lang.Math.ceil
import java.lang.Math.floor
import java.util.regex.Pattern
import kotlin.concurrent.thread


class StViewModel() : ViewModel() {
    object HttpUtil {
        fun sendOkHttpRequest(address: String, callback: Callback) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(address)
                .build()
            client.newCall(request).enqueue(callback)
        }
    }

    fun getWeatherFromOkHttp(httpUrl: String) {
        HttpUtil.sendOkHttpRequest(httpUrl, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("StViewModel", "connecting internet on success")
                thread {
                    try {
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            parseJSONWithGSON(responseData)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("StViewModel", "connecting internet on failure")
                e.printStackTrace()
            }
        })
    }

    val weatherReturnToFragment = MutableLiveData<Intent>()


    private fun parseJSONWithGSON(jsonData: String) {
        val gson = Gson()
        val weather = gson.fromJson(jsonData, WeatherDetailsBean::class.java)
        // 使用LiveData让view对viewModel中值的改变进行监听
        // 通过Intent传递信息
        val intent = Intent().apply {
            putExtra("temperatureNum", weather.result.realtime.temperature)
            putExtra("humidityNum", weather.result.realtime.humidity)
            putExtra("temperature", Tools.convertDoubleToIntByRounding(weather.result.realtime.temperature)
                .toString()
                    + "°")
            putExtra("weather",
                Tools.convertEnglishWeatherToChinese(weather.result.realtime.skycon)
            )
            putExtra("humidity", (weather.result.realtime.humidity * 100).toInt().toString()
                    + "%")
            putExtra("windSpeed", weather.result.realtime.wind.speed)
        }
        weatherReturnToFragment.postValue(intent)
        Log.d("Weather", "temperature is ${weather.result.realtime.temperature}")
    }

    object Tools {
        fun convertStringToPureNumber(oldString: String) : String {
            val newString = StringBuffer()
            //使用正则表达式， 让字符中只留下 负号 和 数字
            val matcher = Pattern.compile("-?\\d").matcher(oldString)
            while (matcher.find()) {
                newString.append(matcher.group())
            }
            return newString.toString()
        }
        // 四舍五入
        fun convertDoubleToIntByRounding(doubleNum : Double) : Int {
            val floorNum = kotlin.math.floor(doubleNum)
            return if(doubleNum - floorNum >= 0.5) {
                kotlin.math.ceil(doubleNum).toInt()
            } else {
                floorNum.toInt()
            }
        }

        fun convertEnglishWeatherToChinese(englishString : String) : String = when(englishString) {
            "CLEAR_DAY" -> "晴"
            "CLEAR_NIGHT" -> "晴"
            "PARTLY_CLOUDY_DAY" -> "多云"
            "PARTLY_CLOUDY_NIGHT" -> "多云"
            "CLOUDY" -> "阴"
            "LIGHT_HAZE" -> "轻度雾霾"
            "MODERATE_HAZE" -> "中度雾霾"
            "HEAVY_HAZE" -> "重度雾霾"
            "LIGHT_RAIN" -> "小雨"
            "MODERATE_RAIN" -> "中雨"
            "HEAVY_RAIN" -> "大雨"
            "STORM_RAIN" -> "暴雨"
            "FOG" -> "雾"
            "LIGHT_SNOW" -> "小雪"
            "MODERATE_SNOW" -> "中雪"
            "HEAVY_SNOW" -> "大雪"
            "STORM_SNOW" -> "暴雪"
            "DUST" -> "浮尘"
            "SAND" -> "沙尘"
            "WIND" -> "大风"
            else -> {
                Log.e("getting weather", "There is no corresponding weather.")
                "ERROR"
            }
        }
    }

    /*
    @RequiresApi(Build.VERSION_CODES.N)
    internal object LocationConsumer : Consumer<Location> {
        override fun accept(t: Location) {
            TODO("Not yet implemented")
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun andThen(after: Consumer<in Location>?): Consumer<Location> {
            return super.andThen(after)
        }
    }

    internal object LocationExecutor : Executor {
         override fun execute(r: Runnable?) {
            Thread(r).start()
        }
    }

    fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                getApplication<Application>(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication<Application>(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getLocation() {
        /*
        //提供位置定位服务的位置管理器对象,中枢控制系统
        val locationManager = getApplication<Application>().getSystemService(LOCATION_SERVICE) as LocationManager
        val locationProvider = LocationManager.NETWORK_PROVIDER
        getLocationPermission()
        val location = if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(0,
                arrayOf1(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
            locationManager.getCurrentLocation(locationProvider, null, LocationExecutor, LocationConsumer)
        val latitude = location.latitude
        //获取经度
        val longitude = location.longitude
        Log.e("Latitude", latitude.toString())
        Log.e("Longitude", longitude.toString()) */
    }*/
}


