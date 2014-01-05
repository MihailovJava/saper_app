package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.adapter.PrayTime;
import com.comfymobile.saadat.database.LocalDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NamasActivity extends Activity {

    Button back;
    Context context;
    String date;

    Spinner city;

    Cursor citySource;

    TextView dateView;

    TextView n1;
    TextView n2;
    TextView n3;
    TextView n4;
    TextView n5;
    TextView n6;

    int utc = 4;
    double lat = 55.751667;
    double lon = 37.617778;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.namas);
        context = this;
        date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        initUI();
    }
    void initUI(){
        n1 = (TextView) findViewById(R.id.n1);
        n2 = (TextView) findViewById(R.id.n2);
        n3 = (TextView) findViewById(R.id.n3);
        n4 = (TextView) findViewById(R.id.n4);
        n5 = (TextView) findViewById(R.id.n5);
        n6 = (TextView) findViewById(R.id.n6);
        city = (Spinner) findViewById(R.id.spinner);
        citySource = LocalDatabase.getInstance(this).getCitySource();
        SimpleCursorAdapter cityAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_item,
                citySource,
                new String[] {"name","_id"},
                new int[] { R.id.name});
        cityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
       // city.setAdapter(cityAdapter);
     /*   city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateTimes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Code here
            }
        });*/

        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dateView = (TextView) findViewById(R.id.date);
        dateView.setText("Сегодня: "+date);

        updateTimes();
    }



    void updateTimes(){

        getCityID();

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

        n1.setText(prayerTimes.get(0));
        n2.setText(prayerTimes.get(1));
        n3.setText(prayerTimes.get(2));
        n4.setText(prayerTimes.get(3));
        n5.setText(prayerTimes.get(5));
        n6.setText(prayerTimes.get(6));
    }

    void getCityID(){
        //int cityPosition = city.getSelectedItemPosition();
        int cityPosition = 0;
        if (cityPosition != -1){
            citySource.moveToPosition(cityPosition);
            utc = citySource.getInt(citySource.getColumnIndex("tzone"));
            lat = Double.valueOf(citySource.getString(citySource.getColumnIndex("x")));
            lon = Double.valueOf(citySource.getString(citySource.getColumnIndex("y")));
        }
    }
}
