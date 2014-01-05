package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;

/**
 * User: Nixy
 * Date: 30.04.13
 * Time: 23:30
 */
public class NewsActivity extends Activity {

    Cursor listSource;
    Context context;
    Button back;
    boolean isNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news);
        context = this;
        isNews = getIntent().getBooleanExtra("news",true);
        initUI();
    }
    void initUI(){
        SimpleCursorAdapter listAdapter;
        ListView list = (ListView) findViewById(R.id.listView);

        if (isNews){
         listSource = LocalDatabase.getInstance(this).getNewsSource();
         listAdapter = new SimpleCursorAdapter(this,
                R.layout.news_item,
                listSource,
                new String[] {"name","source_text","_id"},
                new int[] { R.id.title, R.id.text});
         list.setAdapter(listAdapter);
        } else {
            listSource = LocalDatabase.getInstance(this).getEvents(-1);
            listAdapter = new SimpleCursorAdapter(this,
                    R.layout.event_item,
                    listSource,
                    new String[] {"title","name","time","_id"},
                    new int[] { R.id.title, R.id.city, R.id.date});
            list.setAdapter(listAdapter);
        }


        list.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isNews){
                    int newsPosition = i;
                    listSource.moveToPosition(newsPosition);
                    int newsID = listSource.getInt(listSource.getColumnIndex("_id"));
                    String name = listSource.getString(listSource.getColumnIndex("name"));
                    String caption = listSource.getString(listSource.getColumnIndex("source_text"));
                    Intent intent = new Intent(context, NewsListActivity.class);
                    intent.putExtra("id", newsID);
                    intent.putExtra("name",name);
                    intent.putExtra("caption",caption);
                    context.startActivity(intent);
                }else{
                    int newsPosition = i;
                    listSource.moveToPosition(newsPosition);
                    int newsID = listSource.getInt(listSource.getColumnIndex("_id"));
                    Intent intent = new Intent(context, EventActivity.class);
                    intent.putExtra("id", newsID);
                    intent.putExtra("news",isNews);
                    context.startActivity(intent);
                }

            }
        });
        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
