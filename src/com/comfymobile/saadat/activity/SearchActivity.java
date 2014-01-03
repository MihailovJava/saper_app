package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;


public class SearchActivity extends Activity {

    Button search;
    Button back;
    Context context;

    Spinner city;
    Spinner category;

    Cursor citySource;
    Cursor categorySource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search);
        context = this;
        search = (Button) findViewById(R.id.button);
        search.setOnClickListener(searchEvent);
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
                updateCategories();
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

        category = (Spinner) findViewById(R.id.spinner1);
        updateCategories();
    }

    void updateCategories(){
        categorySource = LocalDatabase.getInstance(this).getCategorySource(getCityID());
        SimpleCursorAdapter categoryAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_item,
                categorySource,
                new String[] {"name","_id"},
                new int[] { R.id.name });
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        category.setAdapter(categoryAdapter);
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

    int getCategoryID(){
        int categoryPosition = category.getSelectedItemPosition();
        categorySource.moveToPosition(categoryPosition);
        int categoryID = categorySource.getInt(categorySource.getColumnIndex("_id"));
        return categoryID;
    }

    View.OnClickListener searchEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try{
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("cityID", getCityID());
                intent.putExtra("categoryID", getCategoryID());
                String categoryName = categorySource.getString(LocalDatabase.CATEGORY_NAME_IND);
                String cityName = citySource.getString(LocalDatabase.CITY_NAME_IND);
                EasyTracker.getInstance(context).send(MapBuilder
                        .createEvent("ui_action", "categorySelect", "Категория =" + categoryName + " Город = " + cityName, null)
                        .build());
                context.startActivity(intent);
            }catch (Exception e){

            }
        }
    };

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
