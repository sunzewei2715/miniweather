package com.exmaple.miniweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.exmaple.miniweather.model.City;
import com.exmaple.miniweather.model.County;
import com.exmaple.miniweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 16/2/5.
 */
public class MiniWeatherDB {

    private static final String DB_NAME="MiniWeather";
    private static final int VERSION=1;
    private SQLiteDatabase db;
    private static MiniWeatherDB miniWeatherDB;

    private MiniWeatherDB(Context context){
        MiniWeatherOpenHelper dbHelper=new MiniWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }

    public synchronized static MiniWeatherDB getInstance(Context context){
        if(miniWeatherDB==null) {
            miniWeatherDB = new MiniWeatherDB(context);
        }
        return miniWeatherDB;
    }

    //存省
    public void saveProvince(Province province){
        ContentValues values=new ContentValues();
        values.put("province_code",province.getProvinceCode());
        values.put("province_name", province.getProvinceName());
        db.insert("Province", null, values);
    }

    //取省列表
    public List<Province> loadProvince(){
        List<Province> list=new ArrayList<Province>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);

            }while(cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    //存市
    public void saveCity(City city){
        ContentValues values=new ContentValues();
        values.put("city_name",city.getCityName());
        values.put("city_code",city.getCityCode());
        values.put("province_id",city.getProvinceId());
        db.insert("City",null,values);
    }

    //取市列表
    public List<City> loadCity(int provinceId){
        List<City> list=new ArrayList<City>();
        Cursor cursor=db.query("City",null,"province_id=?",new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    //存县
    public void saveCounty(County county){
        ContentValues values=new ContentValues();
        values.put("county_name",county.getCountyName());
        values.put("county_code",county.getCountyCode());
        values.put("city_id",county.getCityId());
        db.insert("County",null,values);
    }

    //取县列表
    public List<County> loadCounty(int cityId){
        List<County> list=new ArrayList<County>();
        Cursor cursor=db.query("County",null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    //存"我的城市"
    public void saveMyCity(String newCity){
        ContentValues values=new ContentValues();
        values.put("county_name",newCity.substring(6));
        values.put("county_code",newCity.substring(0, 6));
        db.insert("My_cities",null,values);
    }

    //取"我的城市"列表
    public List<County> loadMyCity(){
        List<County> list=new ArrayList<County>();
        Cursor cursor=db.query("My_cities",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                list.add(county);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    //清空"我的城市"列表
    public void clearMyCity(){
        db.delete("My_cities",null,null);
    }
}
