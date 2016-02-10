package com.exmaple.miniweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import com.exmaple.miniweather.activity.WeatherActivity;
import com.exmaple.miniweather.receiver.AutoUpdateReceiver;
import com.exmaple.miniweather.util.HttpCallBackListener;
import com.exmaple.miniweather.util.HttpUtil;
import com.exmaple.miniweather.util.Utility;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
        int oneHour=60*60*1000;//一小时更新一次
        long triggerTime= SystemClock.elapsedRealtime()+oneHour;
        Intent i=new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi= PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    public void updateWeather(){
        SharedPreferences preferences=getSharedPreferences("data",MODE_PRIVATE);
        String weatherCode=preferences.getString("weather_code", "");
        final String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,address);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AutoUpdateService.this,"后台更新失败",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

}
