package com.comfymobile.saadat.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.adapter.PrayTime;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NamasActivity extends SherlockActivity {

    Button back;
    Context context;
    String date;

    Spinner city;
    LocalDatabase database;
    Cursor citySource;
    Cursor namas;
    TextView dateView;

    CheckBox[] flags;
    TextView[] n;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = LocalDatabase.getInstance(context);
        setContentView(R.layout.namas);
        context = this;
        date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        initUI();
    }
    void initUI(){
        n = new TextView[]{
            (TextView) findViewById(R.id.n1),
            (TextView) findViewById(R.id.n2),
            (TextView) findViewById(R.id.n3),
            (TextView) findViewById(R.id.n4),
            (TextView) findViewById(R.id.n5),
            (TextView) findViewById(R.id.n6)
        };

        flags = new CheckBox[]{
                (CheckBox) findViewById(R.id.alarm_fajr),
                (CheckBox) findViewById(R.id.alarm_sunrise),
                (CheckBox) findViewById(R.id.alarm_duhr),
                (CheckBox) findViewById(R.id.alarm_asr),
                (CheckBox) findViewById(R.id.alarm_maghrib),
                (CheckBox) findViewById(R.id.alarm_isha)
        };

        for (int i = 0; i < flags.length; i++){
            flags[i].setTag(i);
            flags[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int i = (Integer) buttonView.getTag();
                    database.updateNamasFlag(i + 1, isChecked ? 1 : 0);
                }
            });
            namas = database.getNamas(i+1)  ;
            n[i].setText(PrayTime.getNamasTimeFromMillis(namas.getString(namas.getColumnIndex("time"))));
            flags[i].setChecked(namas.getInt(namas.getColumnIndex("flag")) == 0 ? false : true);
        }
        city = (Spinner) findViewById(R.id.spinner);
        citySource = LocalDatabase.getInstance(this).getCitySource();
        SimpleCursorAdapter cityAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_item,
                citySource,
                new String[] {"name","_id"},
                new int[] { R.id.name});
        cityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        dateView = (TextView) findViewById(R.id.date);
        dateView.setText("Сегодня: " + date);
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //needs import android.view.MenuItem;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }
}
