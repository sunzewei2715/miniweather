package com.exmaple.miniweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exmaple.miniweather.R;
import com.exmaple.miniweather.service.AutoUpdateService;
import com.exmaple.miniweather.util.HttpCallBackListener;
import com.exmaple.miniweather.util.HttpUtil;
import com.exmaple.miniweather.util.Utility;

import java.net.HttpURLConnection;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout weatherInfoLayout;
    private TextView cityName;//城市名字
    private TextView weather;//天气
    private TextView temp1;
    private TextView temp2;
    private TextView publishTime;//发布时间
    private TextView currentDate;//日期
    private Button switchCity;//转换城市
    private Button refreshWeather;//更新天气
    private Button myCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
        cityName=(TextView) findViewById(R.id.city_name);
        weather=(TextView) findViewById(R.id.weather);
        temp1=(TextView) findViewById(R.id.temp1);
        temp2=(TextView) findViewById(R.id.temp2);
        publishTime=(TextView) findViewById(R.id.publish_text);
        currentDate=(TextView) findViewById(R.id.current_date);
        switchCity=(Button) findViewById(R.id.switch_city);
        refreshWeather=(Button) findViewById(R.id.refresh_weather);
        myCity=(Button) findViewById(R.id.my_city);
        String countyCode=getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            publishTime.setText("同步中");
            cityName.setVisibility(View.INVISIBLE);
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else{
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        myCity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishTime.setText("同步中");
                SharedPreferences preferences=getSharedPreferences("data",MODE_PRIVATE);
                String weatherCode=preferences.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            case R.id.my_city:
                Intent intent1=new Intent(this,MyCityActivity.class);
                startActivity(intent1);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 查询县级代号所对应的天气代号
     */
    private void queryWeatherCode(String countyCode){
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address, "countyCode");
    }

    /**
     * 查询天气代号对应的天气信息
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address, "weatherCode");
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
     */
    private void queryFromServer(final String address, final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                switch (type) {
                    case "countyCode":
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                        break;
                    case "weatherCode":
                        Utility.handleWeatherResponse(WeatherActivity.this, response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishTime.setText("同步失败");
                    }
                });

            }
        });
    }

    private void showWeather(){
        SharedPreferences preferences=getSharedPreferences("data",MODE_PRIVATE);
        cityName.setText(preferences.getString("city_name",""));
        weather.setText(preferences.getString("weather",""));
        temp1.setText(preferences.getString("temp1",""));
        temp2.setText(preferences.getString("temp2",""));
        publishTime.setText("今天"+preferences.getString("publish_time","")+"发布");
        currentDate.setText(preferences.getString("current_date", ""));
        cityName.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

}
