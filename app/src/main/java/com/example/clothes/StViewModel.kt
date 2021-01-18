package com.example.clothes

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Activity
import android.app.Application
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.regex.Pattern
import kotlin.concurrent.thread


data class Weather(val data : Data, val status : Int, val desc : String) {
    data class Data(val yesterday : Yesterday, val city : String, val forecast : List<FutureWeather>, val ganmao : String, val wendu : String)
    data class Yesterday(val date : String, val high : String, val fx : String, val low : String, val fl : String, val type : String)
    data class FutureWeather(val date : String, val high : String, val fengli : String, val low : String, val fengxiang : String, val type : String)
}


class StViewModel() : ViewModel() {
    object HttpUtil {
        fun sendOkHttpRequest(address: String, callback : Callback) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(address)
                .build()
            client.newCall(request).enqueue(callback)
        }
    }

    fun getWeatherFromOkHttp(httpUrl : String) {
        HttpUtil.sendOkHttpRequest(httpUrl, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                thread {
                    try {
                        val responseData = response.body?.string()
                        if(responseData != null) {
                            parseJSONWithGSON(responseData)
                        }
                    } catch(e : Exception) {
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    val weatherReturnToFragment = MutableLiveData<Intent>()

    fun stringToPureNumber(oldString : String) : String {
        val newString = StringBuffer()
        //使用正则表达式， 让字符中只留下 负号 和 数字
        val matcher = Pattern.compile("-?\\d").matcher(oldString)
        while (matcher.find()) {
            newString.append(matcher.group())
        }
        return newString.toString()
    }

    private fun parseJSONWithGSON(jsonData: String) {
        val gson = Gson()
        val weather = gson.fromJson(jsonData, Weather::class.java)
        // 使用LiveData让view对viewModel中值的改变进行监听
        // 通过Intent传递信息
        weatherReturnToFragment.postValue(Intent().apply {


            putExtra("high", stringToPureNumber(weather.data.forecast[0].high)+"°")
            putExtra("low", stringToPureNumber(weather.data.forecast[0].low)+"°")
            putExtra("type", weather.data.forecast[0].type)
        })

        Log.d("Weather", "high is ${weather.data.forecast[0].high}")
        Log.d("Weather", "fengli is ${weather.data.forecast[0].fengli}")
        Log.d("Weather", "low is ${weather.data.forecast[0].low}")
        Log.d("Weather", "type is ${weather.data.forecast[0].type}")
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


