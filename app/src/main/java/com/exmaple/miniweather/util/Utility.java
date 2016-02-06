package com.exmaple.miniweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.exmaple.miniweather.model.City;
import com.exmaple.miniweather.model.County;
import com.exmaple.miniweather.db.MiniWeatherDB;
import com.exmaple.miniweather.model.Province;

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

}
