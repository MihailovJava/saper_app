package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;


public class InfoActivity extends SherlockActivity {

    Button back;
    Button site;
    Button email;
    TextView version;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.ab_info_title);

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
        String app_ver = "Версия: ";
        version = (TextView)findViewById(R.id.version);
        try
        {
            app_ver += this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {}
        version.setText(app_ver);
        site = (Button) findViewById(R.id.site);
        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://saadat.ru";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        email = (Button) findViewById(R.id.email);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] rec = {"verdana@list.ru"};
                Intent it = new Intent(Intent.ACTION_SEND);
                it.putExtra(Intent.EXTRA_EMAIL, rec);
                it.putExtra(Intent.EXTRA_SUBJECT, "Обратная связь пользователя");
                it.setType("message/rfc822");
                startActivity(Intent.createChooser(it, "Выберите почтовый клиент"));
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
