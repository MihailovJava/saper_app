package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;

/**
 * User: Nixy
 * Date: 01.05.13
 * Time: 22:07
 */
public class EventActivity extends SherlockActivity {

    TextView text;
    Button back;
    Cursor sourceEvent;
    int currentID;
    int id_s;
    boolean isNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.event);
        Intent intent = getIntent();
        currentID = intent.getIntExtra("id",-1);
        id_s = intent.getIntExtra("id_s",0);
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
        info.append("<b><h4>");
        info.append(sourceEvent.getString(sourceEvent.getColumnIndex("title")));
        info.append("</b></h4>");
        if (isNews){
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("last_mod")));
            info.append("<br>");
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("news_text")));
        }else{
            info.append("<em>");
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("time")));
            info.append("<br><br>Адрес: ");
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("address")));
            info.append("</em><br><br>");
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("events_text")));

        }
        text = (TextView) findViewById(R.id.text);
        text.setText(Html.fromHtml(info.toString()));


    }

}
