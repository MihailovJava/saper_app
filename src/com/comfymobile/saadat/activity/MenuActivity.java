package com.comfymobile.saadat.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockActivity;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.comfymobile.saadat.json.RequestSync;
import com.comfymobile.saadat.service.SaadatService;
import com.google.analytics.tracking.android.EasyTracker;

import java.util.Locale;

/**
 * User: Nixy
 * Date: 29.04.13
 * Time: 21:26
 */
public class MenuActivity extends SherlockActivity {

    Button news_button;
    Button namaz_button;
    Button afisha_button;
    Button radio_button;
    Button catalog_button;
    Button info_button;
    ImageView settings;

    private Context context;

    private void initUI(){

        news_button = (Button) findViewById(R.id.news_button);
        namaz_button = (Button) findViewById(R.id.namaz_button);
        afisha_button = (Button) findViewById(R.id.afisha_button);
        radio_button = (Button) findViewById(R.id.radio_button);
        catalog_button = (Button) findViewById(R.id.list_button);
        info_button =   (Button) findViewById(R.id.info_button);
        settings =   (ImageView) findViewById(R.id.settings);

        news_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,SourceListActivity.class);
                context.startActivity(intent);
            }
        });

        namaz_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,NamasActivity.class);
                context.startActivity(intent);
            }
        });

        afisha_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,NewsListActivity.class);
                intent.putExtra("news",false);
                context.startActivity(intent);
            }
        });

        radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,RadioActivity.class);
                context.startActivity(intent);
            }
        });

        catalog_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(context,SearchActivity.class);
               context.startActivity(intent);
            }
        });

        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,InfoActivity.class);
                context.startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SettingsActivity.class);
                context.startActivity(intent);
            }
        });

        if (!isMyServiceRunning())
            context.getApplicationContext().startService(new Intent(context.getApplicationContext(), SaadatService.class));
    }

    boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SaadatService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*setContentView(R.layout.menu);
        initUI();*/

        String country_id = PreferenceManager.getDefaultSharedPreferences(context).getString("country_id", "1");
        Cursor country = LocalDatabase.getInstance(this).getCountryName(Integer.valueOf(country_id));
        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        if (country != null && country.getCount() > 0)
            conf.locale = new Locale(country.getString(country.getColumnIndex("country")).toLowerCase());
        res.updateConfiguration(conf, dm);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.menu);
        initUI();
        new RequestSync(context).execute();
        if (!isNotDatabase()){
            if (!isOnline()){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Требуется обновление, включите интернет соединение")
                        .setNegativeButton("Выйти", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences preferences;
                                SharedPreferences.Editor editor;
                                preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                editor = preferences.edit();
                                editor.putInt("update", 0);
                                editor.commit();
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else  {
                SharedPreferences preferences;
                SharedPreferences.Editor editor;
                preferences = PreferenceManager.getDefaultSharedPreferences(context);
                editor = preferences.edit();
                editor.putInt("update", 0);
                editor.commit();
                Intent intent = new Intent(context,LoadingActivity.class);
                startActivity(intent);
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private boolean isNotDatabase(){
        boolean result = true;
        LocalDatabase db = LocalDatabase.getInstance(this);
        Cursor organizations = db.getListSource(-1,-1);
        Cursor city = db.getCitySource(-1);
        Cursor country = db.getCountryList(-1);
        Cursor category = db.getCategorySource(-1);
        if (organizations.getCount() == 0) result = false;
        if (city.getCount() == 0) result = false;
        if (country.getCount() == 0) result = false;
        if (category.getCount() == 0) result = false;

        return result;
    }

}


