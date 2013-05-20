package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class NamasActivity extends Activity {

    Button back;
    Context context;
    String date;

    Spinner city;

    Cursor citySource;

    TextView dateView;

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
        city = (Spinner) findViewById(R.id.spinner);
        citySource = LocalDatabase.getInstance(this).getCitySource();
        SimpleCursorAdapter cityAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_item,
                citySource,
                new String[] {"name","_id"},
                new int[] { R.id.name});
        cityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        city.setAdapter(cityAdapter);
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Code here
            }
        });

        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dateView = (TextView) findViewById(R.id.date);
        dateView.setText("Сегодня: "+date);
    }

    int getCityID(){
        int cityPosition = city.getSelectedItemPosition();
        if (cityPosition != -1){
            citySource.moveToPosition(cityPosition);
            int cityID = citySource.getInt(citySource.getColumnIndex("_id"));
            return cityID;
        }
        return -1;
    }

    View.OnClickListener searchEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try{
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("cityID", getCityID());
                context.startActivity(intent);
            }catch (Exception e){

            }
        }
    };
}
