package com.comfymobile.saadat.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;


public class SearchActivity extends SherlockActivity {


    Context context;
    ListView category;
    Cursor cityCursor;
    Cursor categorySource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search);
        context = this;




        cityCursor = LocalDatabase.getInstance(context).getCitySource(getCityID());
        String cityName = cityCursor.getString(cityCursor.getColumnIndex("name"));


        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(getString(R.string.ab_locations_title) + "-" + cityName );

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

        category = (ListView) findViewById(R.id.listView);
        updateCategories();
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

    void updateCategories(){
        categorySource = LocalDatabase.getInstance(this).getCategorySource(getCityID());
        SimpleCursorAdapter categoryAdapter = new SimpleCursorAdapter(this,
                R.layout.org_list_item,
                categorySource,
                new String[] {"name","_id"},
                new int[] { R.id.name });
        category.setAdapter(categoryAdapter);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, OrganizationListActivity.class);
                intent.putExtra("cityID", getCityID());
                categorySource.moveToPosition(position);
                int categoryID = categorySource.getInt(categorySource.getColumnIndex("_id"));
                intent.putExtra("categoryID", categoryID);
                String categoryName = categorySource.getString(LocalDatabase.CATEGORY_NAME_IND);
                String cityName = cityCursor.getString(LocalDatabase.CITY_NAME_IND);
                EasyTracker.getInstance(context).send(MapBuilder
                        .createEvent("ui_action", "categorySelect", "Категория = " + categoryName + " Город = " + cityName, null)
                        .build());

                intent.putExtra("categoryName",categoryName);
                context.startActivity(intent);
            }
        });
    }

    int getCityID(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int cityId = Integer.valueOf(preferences.getString("city_id", "12"));
        return cityId;
    }



}
