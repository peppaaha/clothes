package com.example.clothes;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.clothes.stSonActivity.BaseAcitivity;
import com.example.clothes.stSonActivity.WeatherBean;
import com.google.gson.Gson;

import java.util.List;

import static android.text.TextUtils.lastIndexOf;

public class SearchCityActivity extends BaseAcitivity implements View.OnClickListener {

    EditText searchEt;
    ImageView submitIv;
    GridView searchGv;
    String[]hotCities = {"北京","上海","广州","深圳","重庆","苏州","成都","杭州","武汉","南京","西安","天津","郑州","长沙","东莞","沈阳","青岛","合肥","佛山","明光桥中学附属大学"};
//    String[]hotCitys = {"北京","上海","广州","深圳","明光桥中学附属大学","太原","石家庄","哈尔滨","武汉","庄里","伦敦玛丽女王大学","保定","北京邮电大学昌平校区"};
    private ArrayAdapter<String> adapter;
    String url1 = "https://api.caiyunapp.com/v2/place?query=";
    String url2 = "&token=C4JPhPDPmukH7xBe&lang=zh_CN";
    String city = "北京邮电大学";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city2);

        searchEt = findViewById(R.id.search_et);
        submitIv = findViewById(R.id.search_iv_submit);
        searchGv = findViewById(R.id.search_gv);
        submitIv.setOnClickListener(this);
        adapter = new ArrayAdapter<>(this, R.layout.item_hotcity, hotCities);
        searchGv.setAdapter(adapter);

        setListener();
    }

    private void setListener() {
        searchGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = hotCities[position];
                if(city == "明光桥中学附属大学") {
                    city = "北京邮电大学";
                }
                city += "市";
                SharedPreferences.Editor editor = getApplication().getSharedPreferences("temp_weather", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                String url = url1+city+url2;
                loadData(url);
            }
        });

    }


    @Override
    public void onClick(View v){
        Log.d("SearchCityActivity", String.valueOf(v.getId()));
        switch (v.getId()) {
            case R.id.search_iv_submit:
                Log.d("SearchCityActivity", "search botton is clicked");
                city = searchEt.getText().toString();
                if (!TextUtils.isEmpty(city)) {
                    String url = url1 + city + url2;
                    loadData(url);
                } else {
                    Toast.makeText(this,"输入内容不能为空", Toast.LENGTH_SHORT).show();
                }
                city += "市";
                break;
        }
    }

    @Override
    public void onSuccess(String result) {
        Log.d("SearchCityActivity", "onSuccess");
        WeatherBean weatherBean = new Gson().fromJson(result, WeatherBean.class);
        WeatherBean.PlacesBean placesBean = weatherBean.getPlaces().get(0);
        WeatherBean.PlacesBean.LocationBean locationBean = weatherBean.getPlaces().get(0).getLocation();
        
        //跳转到某页面
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("city",placesBean.getName());
            intent.putExtra("lng",locationBean.getLng());
            intent.putExtra("lat",locationBean.getLat());
            //永久存储
            //TODO ：改写成类 并用JSON存储
            String simplifiedCity = placesBean.getName();
            //把 市 后面的文字藏起来
            //比如 北京市政府 -> 北京市
            simplifiedCity = simplifiedCity.substring(0, lastIndexOf(city, '市') + 1);
            SharedPreferences.Editor editor = getApplication().getSharedPreferences("user_location", Context.MODE_PRIVATE).edit();
            editor.putFloat("lng", (float) locationBean.getLng());
            editor.putFloat("lat", (float) locationBean.getLat());
            editor.putString("city", simplifiedCity);
            editor.apply();
            setResult(RESULT_OK, intent);
            Log.d("SearchCityActivity", "Ready to finish");
            finish();
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        Toast.makeText(this, "您可能没有连接网络...", Toast.LENGTH_SHORT).show();
    }


}