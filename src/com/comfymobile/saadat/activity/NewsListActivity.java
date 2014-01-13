package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;

/**
 * User: Nixy
 * Date: 30.04.13
 * Time: 23:30
 */
public class NewsListActivity extends SherlockActivity {

    Cursor listSource;
    Context context;
    Button back;
    int id_s;
    String name;
    String caption;

    TextView name_source;
    TextView caption_source;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.news_list);
        context = this;
        id_s = getIntent().getIntExtra("id",0);
        name = getIntent().getStringExtra("name");
        caption = getIntent().getStringExtra("caption");
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
        name_source = (TextView) findViewById(R.id.source);
        caption_source = (TextView) findViewById(R.id.caption);
        name_source.setText(name);
        caption_source.setText(caption);
        SimpleCursorAdapter listAdapter;
        ListView list = (ListView) findViewById(R.id.listView);

         listSource = LocalDatabase.getInstance(this).getNews(-1, id_s);
         listAdapter = new SimpleCursorAdapter(this,
                R.layout.news_item,
                listSource,
                new String[] {"title","news_text","_id"},
                new int[] { R.id.title, R.id.text});
         list.setAdapter(listAdapter);

        list.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int newsPosition = i;
                listSource.moveToPosition(newsPosition);
                int newsID = listSource.getInt(listSource.getColumnIndex("_id"));
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("id", newsID);
                intent.putExtra("id_s",id_s);
                context.startActivity(intent);

            }
        });
    }
}
