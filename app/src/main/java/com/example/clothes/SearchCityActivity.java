package com.example.clothes;

import android.content.Intent;
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

public class SearchCityActivity extends BaseAcitivity implements View.OnClickListener{

    EditText searchEt;
    ImageView submitIv;
    GridView searchGv;
    String[]hotCitys = {"北京","上海","广州","深圳","保定","太原","石家庄","哈尔滨","重庆", "武汉"};
    private ArrayAdapter<String> adapter;
    String url1 = "https://api.caiyunapp.com/v2/place?query=";
    String url2 = "&token=C4JPhPDPmukH7xBe&lang=zh_CN";
    String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city2);

        searchEt = findViewById(R.id.search_et);
        submitIv = findViewById(R.id.search_iv_submit);
        searchGv = findViewById(R.id.search_gv);
        submitIv.setOnClickListener(this);

        adapter = new ArrayAdapter<>(this, R.layout.item_hotcity, hotCitys);
        searchGv.setAdapter(adapter);
        setListener();
    }

    private void setListener() {
        searchGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = hotCitys[position];
                String url = url1+city+url2;
                loadData(url);
            }
        });
    }


    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.search_iv_submit:
                city = searchEt.getText().toString();
                if (!TextUtils.isEmpty(city)) {
                    String url = url1 + city + url2;
                    loadData(url);

                }else{
                    Toast.makeText(this,"输入内容不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    public void onSuccess(String result) {
        Log.d("SearchCityActivity", "onSuccess");
        WeatherBean weatherBean = new Gson().fromJson(result, WeatherBean.class);
        WeatherBean.PlacesBean placesBean = weatherBean.getPlaces().get(0);
        //跳转到某页面
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("city",placesBean.getName());
            setResult(RESULT_OK, intent);
            Log.d("SearchCityActivity", "Ready to finish");
            finish();
    }
}