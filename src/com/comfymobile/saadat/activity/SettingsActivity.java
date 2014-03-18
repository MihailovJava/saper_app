package com.comfymobile.saadat.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.*;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.NumberPicker;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.comfymobile.saadat.service.SaadatService;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Nixy on 28.01.14.
 */
public class SettingsActivity extends SherlockPreferenceActivity   {

    Context context;
    String[] offsetString;
    LocalDatabase database;
    SharedPreferences preferences;
    SharedPreferences.Editor edit;
    String[] offsetVal = new String[]{
            String.valueOf(10*60*1000),
            String.valueOf(5*60*1000),
            String.valueOf(1*60*1000),
            String.valueOf(2*1000),
    };
    int[] offsetValint = new int[]{
            (10*60*1000),
            (5*60*1000),
            (1*60*1000),
            (2*1000),
    };

    ListPreference cityList;
    ListPreference offsetList;
    ListPreference countryList;
    PreferenceScreen root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database  = LocalDatabase.getInstance(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        edit = preferences.edit();

        offsetString = new String[]{
                getString(R.string.ten_minutes_offset),
                getString(R.string.five_minutes_offset),
                getString(R.string.one_minutes_offset),
                getString(R.string.in_time_offset),
        };

        context = this;

        createCityPref();
        createOffsetPref();
        createCountryPref();

        root = getPreferenceManager().createPreferenceScreen(this);
        updateRoot();

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    void updateRoot(){
        loadTitles();
        root.addPreference(offsetList);
        root.addPreference(countryList);
        root.addPreference(cityList);
        setPreferenceScreen(root);
    }

    private void loadTitles() {
        getSupportActionBar().setTitle(R.string.ab_settings_title);
        offsetList.setTitle(getString(R.string.offset_title));
        cityList.setTitle(getString(R.string.pref_city_title));
        countryList.setTitle(R.string.pref_country_title);
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

    void createOffsetPref(){
        offsetList = new ListPreference(this);
        offsetList.setKey("alarm_offset");
        offsetList.setEntries(offsetString);
        offsetList.setEntryValues(offsetVal);
        offsetList.setTitle(getString(R.string.offset_title));
        String prefoff = preferences.getString("alarm_offset","2000");
        int prefoffint = Integer.valueOf(prefoff);
        for(int i = 0; i < offsetValint.length; i++){
            if (offsetValint[i] == prefoffint ){
                offsetList.setSummary(offsetString[i]);
                edit.putString("alarm_offset", prefoff);
                edit.commit();
            }
        }

        offsetList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int id = 0;
                for (int i = 0 ; i < offsetVal.length; i++){
                    if (offsetVal[i].compareTo(String.valueOf(newValue))== 0){
                        id = i;
                    }
                }

                Toast notify = Toast.makeText(context,
                        getString(R.string.pref_notify_offset)+" " +offsetString[id],
                        Toast.LENGTH_SHORT);
                notify.show();
                offsetList.setSummary(String.valueOf(offsetString[id]));
                return true;
            }
        });
    }

    void createCityPref(){


        String country_id = PreferenceManager.getDefaultSharedPreferences(context).getString("country_id", "1");
        Cursor country = database.getCountryList(Integer.valueOf(country_id));

        Cursor city = database.getCitySourceByCountry(country.getInt(country.getColumnIndex("_id")));
        context = this;
        String[] sCity = new String[city.getCount()];
        final String[] id = new String[city.getCount()];
        for (int i = 0 ; !city.isAfterLast(); i++){
            sCity[i] = city.getString(city.getColumnIndex("name"));
            id[i] = String.valueOf(city.getInt(city.getColumnIndex("_id")));
            city.moveToNext();
        }

        cityList  = new ListPreference(this);
        cityList.setKey("city_id");
        cityList.setEntries(sCity);
        cityList.setEntryValues(id);
        cityList.setTitle(getString(R.string.pref_city_title));

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
                        getString(R.string.pref_notify_city)+" " +city.getString(city.getColumnIndex("name")),
                        Toast.LENGTH_SHORT);
                notify.show();
                EasyTracker.getInstance(context).send(MapBuilder
                        .createEvent("ui_action", "city changed"
                                , getString(R.string.pref_notify_city)+" " + city.getString(city.getColumnIndex("name"))
                                , null)
                        .build());
                cityList.setSummary(city.getString(city.getColumnIndex("name")));
                return true;
            }
        });
    }

    void createCountryPref(){


        String country_id = PreferenceManager.getDefaultSharedPreferences(context).getString("country_id", "1");
        edit.putString("country_id", country_id);
        edit.commit();

        Cursor country = database.getCountryList(Integer.valueOf("-1"));
        String[] countryNames = new String[country.getCount()];
        String[] id = new String[country.getCount()];
        for (int i = 0; !country.isAfterLast(); i++){
            countryNames[i] = country.getString(country.getColumnIndex("name"));
            id[i] = country.getString(country.getColumnIndex("_id"));
            country.moveToNext();
        }

        countryList = new ListPreference(this);
        countryList.setKey("country_id");
        countryList.setEntries(countryNames);
        countryList.setEntryValues(id);
        countryList.setTitle(R.string.pref_country_title);


        country = database.getCountryName(Integer.valueOf(country_id));
        countryList.setSummary(country.getString(country.getColumnIndex("name")));
        countryList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Integer id = Integer.valueOf((String)newValue);
                Cursor country = database.getCountryName(id);
                Cursor city = database.getCitySourceByCountry(country.getInt(country.getColumnIndex("_id")));

                Resources res = context.getResources();
                // Change locale settings in the app.
                DisplayMetrics dm = res.getDisplayMetrics();
                android.content.res.Configuration conf = res.getConfiguration();
                conf.locale = new Locale(country.getString(country.getColumnIndex("country")).toLowerCase());
                res.updateConfiguration(conf, dm);

                if (city.getCount() != 0){
                    Toast notify = Toast.makeText(context,
                            getString(R.string.pref_notify_city)+" " +city.getString(city.getColumnIndex("name")),
                            Toast.LENGTH_SHORT);
                    notify.show();
                    EasyTracker.getInstance(context).send(MapBuilder
                            .createEvent("ui_action", "city changed"
                                    , getString(R.string.pref_notify_city) + " " + city.getString(city.getColumnIndex("name"))
                                    , null)
                            .build());
                    edit.putString("country_id",(String) newValue).commit();
                    edit.putBoolean("rss_hint",false).commit();
                    edit.putString("city_id", city.getString(city.getColumnIndex("_id"))).commit();

                    root.removeAll();
                    createCityPref();
                    updateRoot();
                }



                Toast notify = Toast.makeText(context,
                        getString(R.string.pref_notify_country)+" " +country.getString(country.getColumnIndex("name")),
                        Toast.LENGTH_SHORT);
                notify.show();
                EasyTracker.getInstance(context).send(MapBuilder
                        .createEvent("ui_action", "country changed"
                                , getString(R.string.pref_notify_country)+" " + country.getString(country.getColumnIndex("name"))
                                , null)
                        .build());
                countryList.setSummary(country.getString(country.getColumnIndex("name")));
                return true;
            }
        });
    }

}
