package com.comfymobile.saadat.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;


public class OrganizationListActivity extends SherlockActivity {

    int currentCity;
    int currentCategory;
    Cursor listSource;
    Context context;
    String categoryName;
    Cursor citySource;
    ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        currentCity = intent.getIntExtra("cityID",-1);
        categoryName = intent.getStringExtra("categoryName");
        currentCategory = intent.getIntExtra("categoryID",-1);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(categoryName);

        setContentView(R.layout.orglist);
        context = this;

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.org_list_ab,menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (null != searchView ){

            ImageView imageView = (ImageView) searchView.findViewById(R.id.abs__search_mag_icon);
            imageView.setImageResource(R.drawable.ab_search);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
        {
            public boolean onQueryTextChange(String newText)
            {
                // this is your adapter that will be filtered
               //.getFilter().filter(newText);

                return true;
            }

            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
              //  adapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
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
        list = (ListView) findViewById(R.id.listView);
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
                Intent intent = new Intent(context, DetalOrganizationActivity.class);
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
