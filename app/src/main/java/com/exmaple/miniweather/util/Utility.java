package com.exmaple.miniweather.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.exmaple.miniweather.model.City;
import com.exmaple.miniweather.model.County;
import com.exmaple.miniweather.db.MiniWeatherDB;
import com.exmaple.miniweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by apple on 16/2/5.
 */
public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static synchronized boolean handleProvinceResponse(MiniWeatherDB miniWeatherDB, String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces=response.split(",");
            if(allProvinces!=null && allProvinces.length>0){
                for(String p:allProvinces){
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    miniWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static synchronized boolean handleCityResponse(MiniWeatherDB miniWeatherDB, String response, int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities=response.split(",");
            if(allCities!=null && allCities.length>0){
                for(String p:allCities){
                    String[] array=p.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    miniWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static synchronized boolean handleCountyResponse(MiniWeatherDB miniWeatherDB, String response, int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties=response.split(",");
            if(allCounties!=null && allCounties.length>0){
                for(String p:allCounties){
                    String[] array=p.split("\\|");
                    County county=new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    miniWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据,并将解析的数据储存到本地
     */
    public static void handleWeatherResponse(Context context, String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weather = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weather,publishTime);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, String cityName, String weatherCode,
                                       String temp1, String temp2, String weather, String publishTime){
        SharedPreferences.Editor editor=context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather",weather);
        editor.putString("publish_time",publishTime);
        editor.putBoolean("city_selected", true);
        SimpleDateFormat date=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
        editor.putString("current_date",date.format(new Date()));
        editor.commit();

    }

}
