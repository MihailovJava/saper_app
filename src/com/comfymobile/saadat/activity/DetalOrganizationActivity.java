package com.comfymobile.saadat.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;


public class DetalOrganizationActivity extends SherlockActivity {

    WebView text;
    TextView category;
    Button back;
    Cursor sourceOrganization;
    int currentID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.detal);
        Intent intent = getIntent();
        currentID = intent.getIntExtra("id",-1);
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

        sourceOrganization = LocalDatabase.getInstance(this).getDetal(currentID);

        StringBuilder info = new StringBuilder();
        info.append("<b>");
        info.append(sourceOrganization.getString(sourceOrganization.getColumnIndex("org_name")));
        info.append("</b><br>");
        info.append("<b>Адрес:</b> ");
        info.append(sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.address")));
        info.append("<br><b>Телефон:</b> ");
        info.append(sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.t_number")));
        info.append("<br><b>E-mail:</b> ");
        info.append("<a href=\"mailto:"+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.email"))+"\">"+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.email"))+"</a> ");
        info.append("<br><b>Сайт:</b> ");
        info.append("<a href=\""+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.site"))+"\">"+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.site"))+"</a>");
        info.append("<br><b><p align=\"justify\">Дополнительная информация:</b><br>");
        info.append(sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.description")));
        info.append("</p>");

        text = (WebView) findViewById(R.id.text);
        text.loadDataWithBaseURL(null, info.toString(), "text/html", "utf-8", null);

        category = (TextView) findViewById(R.id.category);
        category.setText("Категория: "+sourceOrganization.getString(sourceOrganization.getColumnIndex("cat_name")));

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
