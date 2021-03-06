package com.exmaple.miniweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by apple on 16/2/5.
 */
public class MiniWeatherOpenHelper extends SQLiteOpenHelper {

    /**
     * Province表建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "province_code text)";
    /**
     * City表建表语句
     */
    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";
    /**
     * County表建表语句
     */
    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer)";
    /**
     * 我的城市表建表语句
     */
    public static final String CREATE_MY_CITY = "create table My_cities ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text)";

    public MiniWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
        db.execSQL(CREATE_MY_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
