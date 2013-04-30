package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;


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

        category = (Spinner) findViewById(R.id.spinner1);
        categorySource = LocalDatabase.getInstance(this).getCategorySource();
        SimpleCursorAdapter categoryAdapter = new SimpleCursorAdapter(this,
                R.layout.spinner_item,
                categorySource,
                new String[] {"name","_id"},
                new int[] { R.id.name });
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        category.setAdapter(categoryAdapter);
        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    View.OnClickListener searchEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try{
                int cityPosition = city.getSelectedItemPosition();
                citySource.moveToPosition(cityPosition);
                int cityID = citySource.getInt(citySource.getColumnIndex("_id"));

                int categoryPosition = category.getSelectedItemPosition();
                categorySource.moveToPosition(categoryPosition);
                int categoryID = categorySource.getInt(categorySource.getColumnIndex("_id"));

                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("cityID", cityID);
                intent.putExtra("categoryID", categoryID);
                context.startActivity(intent);
            }catch (Exception e){

            }
        }
    };
}
