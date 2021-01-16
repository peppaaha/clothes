package com.example.clothes

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Activity
import android.app.Application
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import java.util.concurrent.Executor
import java.util.function.Consumer
import kotlin.arrayOf as arrayOf1


class StViewModel(application: Application) : AndroidViewModel(application) {
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


