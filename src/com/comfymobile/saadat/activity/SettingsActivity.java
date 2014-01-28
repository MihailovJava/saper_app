package com.comfymobile.saadat.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;

import java.util.ArrayList;

/**
 * Created by Nixy on 28.01.14.
 */
public class SettingsActivity extends PreferenceActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LocalDatabase database = LocalDatabase.getInstance(this);
        Cursor city = database.getCitySource();
        context = this;

        String[] sCity = new String[city.getCount()];
        final String[] id = new String[city.getCount()];
        for (int i = 0 ; !city.isAfterLast(); i++){
            sCity[i] = city.getString(city.getColumnIndex("name"));
            id[i] = String.valueOf(city.getInt(city.getColumnIndex("_id")));
            city.moveToNext();
        }
        ListPreference cityList = new ListPreference(this);
        cityList.setKey("city_id");
        cityList.setEntries(sCity);
        cityList.setEntryValues(id);
        cityList.setTitle(getString(R.string.pref_city_title));
        cityList.setSummary(getString(R.string.pref_city_summary));
        cityList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int id = Integer.valueOf((String)newValue);
                Cursor city = database.getCitySource(id);
                Toast notify = Toast.makeText(context,
                        getString(R.string.pref_notify)+" " +city.getString(city.getColumnIndex("name")),
                        Toast.LENGTH_SHORT);
                notify.show();
                return true;
            }
        });


        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        root.addPreference(cityList);
        setPreferenceScreen(root);
    }


}
