package com.example.clothes

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.text.TextUtils.lastIndexOf
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.clothes.stSonActivity.Realtime
import com.example.clothes.stSonActivity.RemoteServerFilesListBean
import com.example.clothes.stSonActivity.WeatherDetailsBean
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Array
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.thread
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

@Parcelize
data class WeatherString(val temperature: String, val weather: String, val humidity: String): Parcelable

class StViewModel(application: Application) : AndroidViewModel(application) {

    val weatherReturnToFragment = MutableLiveData<Intent>()
    val clothesLevelReturnToFragment = MutableLiveData<Int>()
    val filesListReturnToFragment = MutableLiveData<ArrayList<stFragmentClothes>>()

    companion object HttpUtil {
        fun sendOkHttpRequest(address: String, callback: Callback) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(address)
                .build()
            client.newCall(request).enqueue(callback)
        }
    }

    fun handleFeedback(hotOrColdLevel: Int) { //过热负数 过冷正数
        val calendar = Calendar.getInstance()
        val date = calendar.get(Calendar.DATE)

        val prefs_feedback = getApplication<Application>().getSharedPreferences("hot_and_cold_feedback", Context.MODE_PRIVATE)
        val editor = prefs_feedback.edit()

        val savedDate = prefs_feedback.getInt("date", 0)
        if(savedDate != date) {
            editor.putInt("date", date)
            val hotOrColdLevelTotal = prefs_feedback.getInt("hot_and_cold_feedback_level", 0)
            val fixedLevel = prefs_feedback.getInt("fixed_level", 0)
            when {
                hotOrColdLevelTotal + hotOrColdLevel >= 3 -> {
                    editor?.putInt("hot_and_cold_feedback_level", 0)
                    editor?.putInt("fixed_level", fixedLevel + 1)
                    Log.d("StViewModel", "hotOrColdLevelTotal is 0 now")
                    Log.d("StViewModel", "fixed_level is ${fixedLevel + 1} now")
                }
                hotOrColdLevelTotal + hotOrColdLevel <= -3 -> {
                    editor?.putInt("hot_and_cold_feedback_level", 0)
                    editor?.putInt("fixed_level", fixedLevel - 1)
                    Log.d("StViewModel", "hotOrColdLevelTotal is 0 now")
                    Log.d("StViewModel", "fixed_level is ${fixedLevel + 1} now")
                }
                else -> {
                    editor?.putInt("hot_and_cold_feedback_level", hotOrColdLevelTotal + hotOrColdLevel)
                    Log.d("StViewModel", "hotOrColdLevelTotal is ${hotOrColdLevelTotal + hotOrColdLevel} now")
                }
            }
        }
        editor.apply()
    }

    fun getWeatherByLngAndLat(lng: Double, lat: Double) {
        val url1 = "https://api.caiyunapp.com/v2.5/C4JPhPDPmukH7xBe/"
        val url2 = "/realtime.json"
        val httpUrl = "$url1$lng,$lat$url2"
        Log.d("stFragment", "ready to start viewmodel getWeatherFromOkHttp function")
        getWeatherFromOkHttp(httpUrl)
    }

    private fun parseJSONWithGSON(func: (Gson) -> Unit) {
        val gson = Gson()
        func(gson)
    }

    fun getWeatherFromOkHttp(httpUrl: String) {
        HttpUtil.sendOkHttpRequest(httpUrl, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("StViewModel", "connecting internet on success")
                thread {
                    try {
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            parseJSONWithGSON { gson ->
                                val weather = gson.fromJson(responseData, WeatherDetailsBean::class.java)
                                // 使用LiveData让view对viewModel中值的改变进行监听
                                // 通过Intent传递信息
                                val intent = Intent().apply {
                                    putExtra("realtime", weather.result.realtime)
                                    val weatherString = WeatherString(Tools.convertDoubleToIntByRounding(weather.result.realtime.temperature).toString() + "°",
                                        Tools.convertEnglishWeatherToChinese(weather.result.realtime.skycon),
                                        (weather.result.realtime.humidity * 100).toInt().toString() + "%")
                                    putExtra("weatherString", weatherString)
                                    /*
                                    putExtra("temperatureNum", weather.result.realtime.temperature)
                                    putExtra("humidityNum", weather.result.realtime.humidity)
                                    putExtra("windSpeed", weather.result.realtime.wind.speed)
                                    */
                                }
                                weatherReturnToFragment.postValue(intent)
                                Log.d("Weather", "temperature is ${weather.result.realtime.temperature}")
                            }
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

    var tempArray = ArrayList<stFragmentClothes>()
    fun getRemoteServerFilesListFromOkHttp(httpUrl: String, prefix: String = "") {
        HttpUtil.sendOkHttpRequest(httpUrl, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("StViewModel", "connecting internet on success")
                Log.d("StViewModel", "this time prefix is $prefix")
                onResponseDetail(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("StViewModel", "connecting internet on failure")
                e.printStackTrace()
            }

            private fun onResponseDetail(response: Response) {
                try {
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        parseJSONWithGSON { gson ->
                            val filesList = gson.fromJson(responseData, RemoteServerFilesListBean::class.java)
                            // 使用LiveData让view对viewModel中值的改变进行监听
                            Log.d("StViewModel", "ready to parse JSON (files list)")
                            for (file in filesList) {
                                if(file.type == "file") {
                                    Log.d("StViewModel", "file list url is $httpUrl/${file.name}")
                                    Log.d("StViewModel", "the first is 1, the last is ${lastIndexOf(file.name, '.')}")
                                    if(lastIndexOf(file.name, '.') != -1)
                                        tempArray.add(stFragmentClothes(prefix + file.name.substring(0, lastIndexOf(file.name, '.')),
                                            "$httpUrl/${file.name}"))
                                } else if(file.type == "directory") {
                                    Log.d("StViewModel", "directory list url is $httpUrl/${file.name}")
                                //    getRemoteServerFilesListFromOkHttp("$httpUrl/${file.name}", prefix + file.name)
                                }
                            }
                            filesListReturnToFragment.postValue(tempArray)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }



    fun calcClothesLevel(realtime: Realtime) {
        val prefs_location = getApplication<Application>().getSharedPreferences("user_location", Context.MODE_PRIVATE)
        val lat = prefs_location?.getFloat("lat", 0.0F)
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val sinPart = 1.0 - 0.3 * sin(lat?.minus(23.5F)!!)
        val cosPart = 0.3 * cos((15 * (month - 1)).toDouble())
        val temperatureComfortable = 22.7 * sinPart - abs(cosPart) //T_s 舒适温度
        Log.d("StViewModel", "temperatureComfortable is $temperatureComfortable")
        val temperatureAir = realtime.temperature //T_a 空气温度
        Log.d("StViewModel", "temperatureAir is $temperatureAir")
        var c1 : Double
        var c2 : Double
        var c3 : Double
        var c4 : Double
        if(temperatureAir >= temperatureComfortable) {
            c1 = 1.0
            c2 = 0.05
            c3 = -1.0
            c4 = -0.03
        } else {
            c1 = -1.0
            c2 = -0.013
            c3 = 1.0
            c4 = 0.01
        }
        //1
        val windSpeedNum = realtime.wind.speed
        val humidityNum = realtime.humidity
        val weather = realtime.skycon
        val humidityComfortable = when(weather) {
            "LIGHT_RAIN", "MODERATE_RAIN", "HEAVY_RAIN" -> 0.618
            else -> 0.5
        }
        Log.d("StViewModel", "humidity comfortable is $humidityComfortable")
        var humidityWeight = humidityNum - humidityComfortable
        if(humidityWeight <= 0) humidityWeight = 1.0
        val expPart = c2 * (temperatureAir - temperatureComfortable) * humidityWeight
        val temperatureBody = temperatureAir + c1 * 1.4 * (exp(expPart) + c3)+ c4 * (temperatureAir - temperatureComfortable) * windSpeedNum //T_g 体感温度
        Log.d("StViewModel", "expPart is $expPart")
        Log.d("StViewModel", "expPartAfter is ${exp(expPart)}")
        Log.d("StViewModel", "windSpeed is $windSpeedNum")
        Log.d("StViewModel", "temperatureBody is $temperatureBody")
        //2
        val expPart2 = 17.27 * temperatureAir / (237.7 + temperatureAir)
        val ePart2 = humidityNum / 100 * 6.105 * exp(expPart2)
        val temperatureBody2 = 1.07 * temperatureAir + 0.2 * ePart2 - 0.65 * windSpeedNum -2.7
        Log.d("StViewModel", "standard temperatureBody is $temperatureBody2")
        //
        val delta = 22.7 - temperatureComfortable
        var level : Int
        if(temperatureBody2 > 32 - delta) {
            level = 4
        } else if(temperatureBody > 29 - delta) {
            level = 3
        } else if(temperatureBody > 25 - delta) {
            level = 2
        } else if(temperatureBody > 23 - delta) {
            level = 1
        } else if(temperatureBody > 18 - delta) {
            level = 0
        } else if(temperatureBody > 13 - delta) {
            level = -1
        } else if(temperatureBody > 6 - delta) {
            level = -2
        } else if(temperatureBody > -2 - delta) {
            level = -3
        } else if(temperatureBody > -10 - delta) {
            level = -4
        } else if(temperatureBody > -20 - delta) {
            level = -5
        } else {
            level = -6
        }
        //level fixed by feedback
        val prefs_feedback = getApplication<Application>().getSharedPreferences("hot_and_cold_feedback", Context.MODE_PRIVATE)
        val fixedLevel = prefs_feedback.getInt("fixed_level", 0)
        level += fixedLevel
        if(level > 4) level = 4
        else if(level < -6) level = -6
        clothesLevelReturnToFragment.postValue(level)
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


