package com.comfymobile.saadat.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;


public class SearchActivity extends SherlockActivity {

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.search);
        context = this;
        search = (Button) findViewById(R.id.button);
        search.setOnClickListener(searchEvent);
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
                Intent intent = new Intent(context, OrganizationListActivity.class);
                intent.putExtra("cityID", getCityID());
                intent.putExtra("categoryID", getCategoryID());
                context.startActivity(intent);
            }catch (Exception e){

            }
        }
    };
}
