package com.example.clothes.stSonActivity
// https://api.caiyunapp.com/v2.5/C4JPhPDPmukH7xBe/116.512885,39.847469/realtime.json
data class WeatherDetailsBean(
    val api_status: String,
    val api_version: String,
    val lang: String,
    val location: List<Double>,
    val result: Result,
    val server_time: Int,
    val status: String,
    val timezone: String,
    val tzshift: Int,
    val unit: String
)

data class Result(
    val primary: Int,
    val realtime: Realtime
)

data class Realtime(
    val air_quality: AirQuality,
    val apparent_temperature: Double,
    val cloudrate: Double,
    val dswrf: Double,
    val humidity: Double,
    val life_index: LifeIndex,
    val precipitation: Precipitation,
    val pressure: Double,
    val skycon: String,
    val status: String,
    val temperature: Double,
    val visibility: Double,
    val wind: Wind
)

data class AirQuality(
    val aqi: Aqi,
    val co: Double,
    val description: Description,
    val no2: Double,
    val o3: Double,
    val pm10: Double,
    val pm25: Double,
    val so2: Double
)

data class LifeIndex(
    val comfort: Comfort,
    val ultraviolet: Ultraviolet
)

data class Precipitation(
    val local: Local,
    val nearest: Nearest
)

data class Wind(
    val direction: Double,
    val speed: Double
)

data class Aqi(
    val chn: Double,
    val usa: Double
)

data class Description(
    val chn: String,
    val usa: String
)

data class Comfort(
    val desc: String,
    val index: Int
)

data class Ultraviolet(
    val desc: String,
    val index: Double
)

data class Local(
    val datasource: String,
    val intensity: Double,
    val status: String
)

data class Nearest(
    val distance: Double,
    val intensity: Double,
    val status: String
)

/*
JSON
status : "ok"
api_version : "v2.5"
api_status : "active"
lang : "zh_CN"
unit : "metric"
tzshift : 28800
timezone : "Asia/Shanghai"
server_time : 1611302451
location
result
realtime
status : "ok"
temperature : 4
humidity : 0.44
cloudrate : 0
skycon : "MODERATE_HAZE"
visibility : 3.6
dswrf : 281.7
wind
speed : 5.4
direction : 156
pressure : 102006.64
apparent_temperature : 1.2
precipitation
local
status : "ok"
datasource : "radar"
intensity : 0
nearest
status : "ok"
distance : 10000
intensity : 0
air_quality
pm25 : 129
pm10 : 132
o3 : 25
so2 : 19
no2 : 73
co : 2.2
aqi
chn : 170
usa : 189
description
chn : "中度污染"
usa : "中度污染"
life_index
ultraviolet
index : 2
desc : "很弱"
comfort
index : 12
desc : "湿冷"
primary : 0
 */