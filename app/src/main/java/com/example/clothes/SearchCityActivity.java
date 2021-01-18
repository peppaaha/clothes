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
import android.widget.Toast;
import com.example.clothes.stSonActivity.BaseAcitivity;
import com.example.clothes.stSonActivity.WeatherBean;

public class SearchCityActivity extends BaseAcitivity implements View.OnClickListener{

    EditText searchEt;
    ImageView submitIv;
    GridView searchGv;
    String[]hotCitys = {"北京","上海","广州","深圳","保定","佛山","南京","苏州","厦门",
            "长沙","成都","福州","杭州","武汉","青岛","西安","太原","沈阳","重庆","天津"};
    private ArrayAdapter<String> adapter;
    //  加上网址
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

        adapter = new ArrayAdapter<>(this, R.layout.item_hotcity, hotCitys);
        searchGv.setAdapter(adapter);
        setListener();
    }

    private void setListener() {
        searchGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = hotCitys[position];
                Log.d("SearchCityActivity", "get city");
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
                    //需要那个API
                    //完整网址
                    String url = url1 + city + url2;
                    //调用父类加载网络数据（传入网址）
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
        WeatherBean weatherBean = new WeatherBean();
        if (weatherBean.getError() == 0) {
            //跳转到某页面
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("city",city);
            setResult(RESULT_OK, intent);
            Log.d("SearchCityActivity", "Ready to finish");
            finish();
        } else{
            Toast.makeText(this,"暂时未收入此城市天气信息...",Toast.LENGTH_SHORT).show();
        }
    }
}