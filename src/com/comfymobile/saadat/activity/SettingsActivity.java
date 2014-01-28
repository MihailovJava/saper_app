package com.comfymobile.saadat.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.*;
import android.view.Menu;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.comfymobile.saadat.service.SaadatService;

import java.util.ArrayList;

/**
 * Created by Nixy on 28.01.14.
 */
public class SettingsActivity extends SherlockPreferenceActivity   {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LocalDatabase database = LocalDatabase.getInstance(this);
        Cursor city = database.getCitySource();
        context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String[] sCity = new String[city.getCount()];
        final String[] id = new String[city.getCount()];
        for (int i = 0 ; !city.isAfterLast(); i++){
            sCity[i] = city.getString(city.getColumnIndex("name"));
            id[i] = String.valueOf(city.getInt(city.getColumnIndex("_id")));
            city.moveToNext();
        }
        final ListPreference cityList = new ListPreference(this);
        cityList.setKey("city_id");
        cityList.setEntries(sCity);
        cityList.setEntryValues(id);
        cityList.setTitle(getString(R.string.pref_city_title));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = preferences.edit();
        int cityId = Integer.valueOf(preferences.getString("city_id", "-1"));
        if (cityId < 0){
            edit.putString("city_id", SaadatService.MOSCOW_ID);
            edit.commit();
            cityId = Integer.valueOf(SaadatService.MOSCOW_ID);
        }
        city = database.getCitySource(cityId);
        cityList.setSummary(city.getString(city.getColumnIndex("name")));
        cityList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int id = Integer.valueOf((String)newValue);
                Cursor city = database.getCitySource(id);
                Toast notify = Toast.makeText(context,
                        getString(R.string.pref_notify)+" " +city.getString(city.getColumnIndex("name")),
                        Toast.LENGTH_SHORT);
                notify.show();
                cityList.setSummary(city.getString(city.getColumnIndex("name")));
                return true;
            }
        });


        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        root.addPreference(cityList);
        setPreferenceScreen(root);
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
}
