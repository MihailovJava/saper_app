package com.comfymobile.saadat.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * User: Nixy
 * Date: 01.05.13
 * Time: 22:07
 */
public class DetalNewsActivity extends SherlockActivity {

    WebView text;
    Cursor sourceEvent;
    int currentID;
    int id_s;
    boolean isNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setLogo(R.drawable.ab_back);
        ab.setTitle("");

        setContentView(R.layout.event);
        Intent intent = getIntent();
        currentID = intent.getIntExtra("id",-1);
        id_s = intent.getIntExtra("sourceId",0);
        isNews = intent.getBooleanExtra("news",true);
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

        if (isNews)
         sourceEvent = LocalDatabase.getInstance(this).getNews(currentID,id_s);
        else
         sourceEvent = LocalDatabase.getInstance(this).getEvents(currentID);

        StringBuilder info = new StringBuilder();
        String link = null;
        info.append("<b><h4>");
        info.append(sourceEvent.getString(sourceEvent.getColumnIndex("title")));
        info.append("</b></h4>");
        if (isNews){
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("last_mod")));
            info.append("<br><p align=\"justify\">");
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("news_text")));
            info.append("</p>");
            link = sourceEvent.getString(sourceEvent.getColumnIndex("url"));
        }else{
            info.append("<em>");
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("time")));
            info.append("<br><br>Адрес: ");
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("address")));
            info.append("</em><br><br><p align=\"justify\">");
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("events_text")));
            info.append("</p>");
        }
        text = (WebView) findViewById(R.id.text);
        text.loadDataWithBaseURL(null, info.toString(), "text/html", "utf-8", null);
        Button full = (Button) findViewById(R.id.full);
        if (link != null){
            final String finalLink = link;
            full.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalLink));
                    startActivity(browserIntent);
                }
            });
        }else{
            full.setVisibility(View.GONE);
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

}
