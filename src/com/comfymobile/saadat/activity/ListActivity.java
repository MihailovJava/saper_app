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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;


public class ListActivity extends Activity {

    int currentCity;
    int currentCategory;
    Cursor listSource;
    Cursor citySource;
    Context context;
    Button back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.orglist);
        context = this;
        Intent intent = getIntent();
        currentCity = intent.getIntExtra("cityID",-1);
        currentCategory = intent.getIntExtra("categoryID",-1);
        initUI();
    }
    void initUI(){
        ListView list = (ListView) findViewById(R.id.listView);
        listSource = LocalDatabase.getInstance(this).getListSource(currentCity,currentCategory);
        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this,
                R.layout.list_item,
                listSource,
                new String[] {"name","address","_id"},
                new int[] { R.id.name, R.id.address});
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int orgPosition = i;
                listSource.moveToPosition(orgPosition);
                int orgID = listSource.getInt(listSource.getColumnIndex("_id"));
                Intent intent = new Intent(context, DetalActivity.class);
                intent.putExtra("id", orgID);
                String orgName = listSource.getString(LocalDatabase.ORG_NAME_IND);
                citySource = LocalDatabase.getInstance(context).getCitySource(currentCity);
                String cityName = citySource.getString(LocalDatabase.CITY_NAME_IND);
                EasyTracker.getInstance(context).send(MapBuilder
                        .createEvent("ui_action", "organizationSelect", "Город = " + cityName + " Организация = " + orgName, null)
                        .build());
                context.startActivity(intent);
            }
        });
        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
