package com.comfymobile.saadat.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.comfymobile.saadat.json.OrganizationRequest;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;


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
            case R.id.ab_search_add:
                showAddDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialog = getLayoutInflater().inflate(R.layout.add_organization, null);
        final EditText eName = (EditText) dialog.findViewById(R.id.name);
        final EditText eCategory = (EditText) dialog.findViewById(R.id.category);
        final EditText eAddress = (EditText) dialog.findViewById(R.id.address);
        final EditText eTN = (EditText) dialog.findViewById(R.id.tn);
        final EditText eAddition = (EditText) dialog.findViewById(R.id.addition);
        builder.setView(dialog);
        builder.setNegativeButton("Отмена", null);
        builder.setPositiveButton("Добавить",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String name = eName.getText().toString();
                String category = eCategory.getText().toString();
                String address = eAddress.getText().toString();
                String tn = eTN.getText().toString();
                String additional = eAddition.getText().toString();
                OrganizationRequest request = new OrganizationRequest();
                request.name = name;
                request.category = category;
                request.address = address;
                request.tn = tn;
                request.additional = additional;
                String json = new Gson().toJson(request);
                LocalDatabase.getInstance(context).addRequestOrg(json,request.date.getTime()/1000);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.org_search_ab,menu);

        return super.onCreateOptionsMenu(menu);
    }



}
