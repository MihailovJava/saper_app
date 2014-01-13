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
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;


public class ListActivity extends SherlockActivity {

    int currentCity;
    int currentCategory;
    Cursor listSource;
    Context context;
    Button back;
    Cursor citySource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.orglist);
        context = this;
        Intent intent = getIntent();
        currentCity = intent.getIntExtra("cityID",-1);
        currentCategory = intent.getIntExtra("categoryID",-1);
        initUI();
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

    }
}
