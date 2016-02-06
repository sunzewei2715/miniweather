package com.exmaple.miniweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public void saveProvince(Province province){
        ContentValues values=new ContentValues();
        values.put("provinceName",province.getProvinceName());
        values.put("provinceCode",province.getProvinceCode());
        db.insert("Province",null,values);
    }

    public List<Province> loadProvince(){
        List<Province> list=new ArrayList<Province>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("provinceCode")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
                list.add(province);

            }while(cursor.moveToNext());
        }
        return list;
    }

    public void saveCity(City city){
        ContentValues values=new ContentValues();
        values.put("cityName",city.getCityName());
        values.put("cityCode",city.getCityCode());
        values.put("provinceId",city.getProvinceId());
        db.insert("City",null,values);
    }

    public List<City> loadCity(int provinceId){
        List<City> list=new ArrayList<City>();
        Cursor cursor=db.query("City",null,"province_id=?",new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city=new City();
                city.setCityCode(cursor.getString(cursor.getColumnIndex("cityNode")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("provinceId")));
                list.add(city);
            }while(cursor.moveToNext());
        }
        return list;
    }

    public void saveCounty(County county){
        ContentValues values=new ContentValues();
        values.put("countyName",county.getCountyName());
        values.put("countyCode",county.getCountyCode());
        values.put("cityId",county.getCityId());
        db.insert("County",null,values);
    }

    public List<County> loadCounty(int cityId){
        List<County> list=new ArrayList<County>();
        Cursor cursor=db.query("County",null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county=new County();
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("countyNode")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("countyName")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("cityId")));
                list.add(county);
            }while(cursor.moveToNext());
        }
        return list;
    }
}
