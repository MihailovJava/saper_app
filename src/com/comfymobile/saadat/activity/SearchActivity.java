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

    Button search;
    Context context;
    Spinner category;
    TextView city;
    Cursor cityCursor;
    Cursor categorySource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setLogo(R.drawable.ab_back);
        ab.setTitle(R.string.ab_locations_title);

        setContentView(R.layout.search);
        context = this;

        search = (Button) findViewById(R.id.button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrganizationListActivity.class);
                intent.putExtra("cityID", getCityID());
                intent.putExtra("categoryID", getCategoryID());
                String categoryName = categorySource.getString(LocalDatabase.CATEGORY_NAME_IND);
                String cityName = cityCursor.getString(LocalDatabase.CITY_NAME_IND);
                EasyTracker.getInstance(context).send(MapBuilder
                        .createEvent("ui_action", "categorySelect", "Категория = " + categoryName + " Город = " + cityName, null)
                        .build());

                intent.putExtra("categoryName",categoryName);
                context.startActivity(intent);
            }
        });
        search.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context,MapActivity.class);
                context.startActivity(intent);
                return true;
            }
        });

        cityCursor = LocalDatabase.getInstance(context).getCitySource(getCityID());
        city = (TextView) findViewById(R.id.city_text);
        city.setText(cityCursor.getString(cityCursor.getColumnIndex("name")));
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

        category = (Spinner) findViewById(R.id.spinner1);
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
                R.layout.spinner_item,
                categorySource,
                new String[] {"name","_id"},
                new int[] { R.id.name });
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        category.setAdapter(categoryAdapter);
    }

    int getCityID(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int cityId = Integer.valueOf(preferences.getString("city_id", "12"));
        return cityId;
    }

    int getCategoryID(){
        int categoryPosition = category.getSelectedItemPosition();
        categorySource.moveToPosition(categoryPosition);
        int categoryID = categorySource.getInt(categorySource.getColumnIndex("_id"));
        return categoryID;
    }

}
