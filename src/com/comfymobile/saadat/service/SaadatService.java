package com.comfymobile.saadat.service;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.activity.NamasActivity;
import com.comfymobile.saadat.adapter.PrayTime;
import com.comfymobile.saadat.database.LocalDatabase;

import java.util.*;

/**
 * Created by Nixy on 26.01.14.
 */
public class SaadatService extends Service {

    LocalDatabase database;
    private Context context;
    Cursor pray;
    Cursor city;
    public static final String MOSCOW_ID = "12";


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        database = LocalDatabase.getInstance(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(! database.isLocked() ){
                    checkAlarm();
                    updateNamas();
                }
            }
        },0,1000);

        return Service.START_STICKY;
    }

    void checkAlarm(){
        Calendar currentTime = Calendar.getInstance();
        pray = database.getNamas(-1);
        long currentTimeInMillis = currentTime.getTimeInMillis();
        while (!pray.isAfterLast()){
            Long namasTimeInMillis = Long.valueOf(pray.getString(pray.getColumnIndex("time")));
            int isMiss = pray.getInt(pray.getColumnIndex("miss"));
            int flag = pray.getInt(pray.getColumnIndex("flag"));
            int id = pray.getInt(pray.getColumnIndex("_id"));
            String name = pray.getString(pray.getColumnIndex("name"));
            if ( isTimeToAlarm(id ,namasTimeInMillis , currentTimeInMillis,isMiss,flag) ){

                String text = name;
                Uri soundURI = Uri.parse("android.resource://" +
                        context.getResources().getResourcePackageName(R.raw.lt_notification_sound) + "/" +
                        R.raw.lt_notification_sound);
                NotificationManager nm = (NotificationManager)context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setSound(soundURI)
                        .setContentText(text)
                        .setContentTitle("Alarm")
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                Context c = getApplicationContext();
                PendingIntent actionPendingIntent = PendingIntent.getActivity(c, 0, new Intent(c, NamasActivity.class), 0);
                builder.setContentIntent(actionPendingIntent);
                nm.notify(1000, builder.getNotification());
            }

            pray.moveToNext();
        }
    }

    boolean isTimeToAlarm(int id ,long namasTime,long currentTime,int isMiss,int flag){

        if (flag == 0)
            return false;                   // таймер запущен

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int offset = preferences.getInt("alarm_offset",2000);      // две секунды
        long diff = namasTime - currentTime + 1000;                  // время до молитвы поправка на милисекунды

        if (diff < 0){                                       // молитва прошла
            database.updateNamasMiss(id, 1);                 // пропустили
            return false;
        }
        if (diff <= offset){                              // до молитвы меньше времени чем зарезервировано
            database.updateNamasMiss(id, 1);
            return true;
        }

        if (isMiss == 1){
            database.updateNamasMiss(id, 0);        // если дошли досюда то до молитвы еще есть время
                                                    // а стоит флаг пропуска
        }
        return false;
    }

    void updateNamas(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int saveDay = preferences.getInt("namasupdate", 0);
        int saveCity = preferences.getInt("namas_city",0);
        int today = (int) (System.currentTimeMillis() / 1000 / 60 / 60 / 24);
        int city_id = Integer.valueOf(preferences.getString("city_id",MOSCOW_ID));

        if ( saveDay != today ||  saveCity != city_id){

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("namasupdate",today);
            editor.commit();
            editor.putInt("namas_city",city_id);
            editor.commit();

            city = database.getCity(city_id);

            double lat = Double.valueOf(city.getString(city.getColumnIndex("y")));
            double lon = Double.valueOf(city.getString(city.getColumnIndex("x")));
            int utc = Integer.valueOf(city.getString(city.getColumnIndex("tzone")));

            PrayTime prayers = new PrayTime();

            prayers.setTimeFormat(prayers.Time24);

            prayers.setCalcMethod(prayers.Karachi);

            prayers.setAsrJuristic(prayers.Shafii);
            prayers.setAdjustHighLats(prayers.AngleBased);
            int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
            prayers.tune(offsets);

            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);

            ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal,
                    lat, lon, utc);

            for (int i = 0,j = 0; i < 6; i ++){
                if (i == 4) j++;
                database.updateNamasTime(i,PrayTime.getNamasTimeInMillis(prayerTimes.get(j++)));
            }
            database.dropNamasMiss();

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
