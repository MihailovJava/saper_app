package com.comfymobile.saadat.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.adapter.NewsAdapter;
import com.comfymobile.saadat.database.LocalDatabase;
import com.comfymobile.saadat.json.EventRequest;
import com.comfymobile.saadat.json.RequestSync;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;

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
            case R.id.ab_event_add:
                showAddDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialog = getLayoutInflater().inflate(R.layout.add_event, null);
        final EditText eTitle = (EditText) dialog.findViewById(R.id.title);
        final EditText eText = (EditText) dialog.findViewById(R.id.text);
        final EditText eTime = (EditText) dialog.findViewById(R.id.time);
        final EditText eCity = (EditText) dialog.findViewById(R.id.city);
        final EditText eAddress = (EditText) dialog.findViewById(R.id.address);
        final EditText eAddition = (EditText) dialog.findViewById(R.id.additional);
        builder.setView(dialog);
        builder.setNegativeButton("Отмена", null);
        builder.setPositiveButton("Добавить",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String title = eTitle.getText().toString();
                String text = eText.getText().toString();
                String time = eTime.getText().toString();
                String city = eCity.getText().toString();
                String address = eAddress.getText().toString();
                String additional = eAddition.getText().toString();
                EventRequest request = new EventRequest();
                request.title = title;
                request.text = text;
                request.time = time;
                request.city = city;
                request.address = address;
                request.additional = additional;
                String json = new Gson().toJson(request);
                LocalDatabase.getInstance(context).addRequest(json,request.date.getTime()/1000, RequestSync.TYPE_EVENTS);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
            int countryId = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("country_id", "1"));
            cursor = LocalDatabase.getInstance(this).getEvents(countryId);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isNews){
            MenuInflater inflater = getSupportMenuInflater();
            inflater.inflate(R.menu.event_list_ab,menu);

            return super.onCreateOptionsMenu(menu);
        }
        return false;
    }
}
