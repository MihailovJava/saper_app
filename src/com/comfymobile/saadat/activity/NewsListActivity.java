package com.comfymobile.saadat.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.adapter.NewsAdapter;
import com.comfymobile.saadat.adapter.RSSReader;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * User: Nixy
 * Date: 30.04.13
 * Time: 23:30
 */
public class NewsListActivity extends SherlockActivity {

    Cursor cursor;
    Context context;
    int sourceID;
    TextView sourceTitleView;
    TextView sourceDescriptionView;
    String sourceTitle;
    boolean isNews;
    String sourceDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        sourceID = getIntent().getIntExtra("sourceId",0);
        sourceTitle = getIntent().getStringExtra("sourceTitle");
        sourceDescription = getIntent().getStringExtra("sourceDescription");
        isNews = getIntent().getBooleanExtra("news",true);
        if (isNews) setContentView(R.layout.news_list); else setContentView(R.layout.news);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        if (isNews)
         ab.setTitle(sourceTitle);
        else
         ab.setTitle(R.string.ab_events_title);

    }

    @Override
    protected void onResume() {
        initUI();
        super.onResume();
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
        CursorAdapter listAdapter;
        ListView list = (ListView) findViewById(R.id.listView);

        if (isNews){

            sourceTitleView = (TextView) findViewById(R.id.source);
            sourceTitleView.setText(sourceTitle);
            sourceDescriptionView = (TextView) findViewById(R.id.caption);
            sourceDescriptionView.setText(sourceDescription);

            cursor = LocalDatabase.getInstance(this).getNews(-1, sourceID);
            listAdapter = new NewsAdapter(this,cursor);
            list.setAdapter(listAdapter);
        } else {
            cursor = LocalDatabase.getInstance(this).getEvents(-1);
            listAdapter = new SimpleCursorAdapter(this,
                    R.layout.event_item,
                    cursor,
                    new String[] {"title","name","time","_id"},
                    new int[] { R.id.title, R.id.city, R.id.date});
            list.setAdapter(listAdapter);
        }


        list.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isNews){
                    int newsPosition = i;
                    cursor.moveToPosition(newsPosition);
                    int newsID = cursor.getInt(cursor.getColumnIndex("_id"));

                    Intent intent = new Intent(context, DetalNewsActivity.class);
                    intent.putExtra("id", newsID);
                    intent.putExtra("sourceId",sourceID);
                    intent.putExtra("news",isNews);
                    context.startActivity(intent);
                    String newsName = cursor.getString(LocalDatabase.NEWS_NAME_IND);
                    String whatThis = " Новость ";
                    EasyTracker.getInstance(context).send(MapBuilder
                            .createEvent("ui_action", "newsSelect", whatThis + " = " + newsName, null)
                            .build());
                }else{
                    int newsPosition = i;
                    cursor.moveToPosition(newsPosition);
                    int newsID = cursor.getInt(cursor.getColumnIndex("_id"));
                    Intent intent = new Intent(context, DetalNewsActivity.class);
                    intent.putExtra("id", newsID);
                    intent.putExtra("news",isNews);
                    context.startActivity(intent);
                    String newsName = cursor.getString(LocalDatabase.NEWS_NAME_IND);
                    String whatThis = " Событие ";
                    EasyTracker.getInstance(context).send(MapBuilder
                            .createEvent("ui_action", "newsSelect", whatThis + " = " + newsName, null)
                            .build());
                }

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
