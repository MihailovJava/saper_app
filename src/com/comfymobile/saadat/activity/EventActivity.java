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
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;

/**
 * User: Nixy
 * Date: 01.05.13
 * Time: 22:07
 */
public class EventActivity extends Activity {

    TextView text;
    Button back;
    Cursor sourceEvent;
    int currentID;
    boolean isNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.event);
        Intent intent = getIntent();
        currentID = intent.getIntExtra("id",-1);
        isNews = intent.getBooleanExtra("news",true);
        initUI();
    }
    void initUI(){

        if (isNews)
         sourceEvent = LocalDatabase.getInstance(this).getNews(currentID);
        else
         sourceEvent = LocalDatabase.getInstance(this).getEvents(currentID);

        StringBuilder info = new StringBuilder();
        info.append(sourceEvent.getString(sourceEvent.getColumnIndex("last_mod")));
        info.append("<br><b><h4>");
        info.append(sourceEvent.getString(sourceEvent.getColumnIndex("title")));
        info.append("</b></h4>");
        if (isNews)
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("news_text")));
        else
            info.append(sourceEvent.getString(sourceEvent.getColumnIndex("events_text")));
        text = (TextView) findViewById(R.id.text);
        text.setText(Html.fromHtml(info.toString()));

        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
