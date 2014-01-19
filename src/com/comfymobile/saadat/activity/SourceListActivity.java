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
public class SourceListActivity extends SherlockActivity {

    Cursor listSource;
    Context context;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.news);
        context = this;
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

        SimpleCursorAdapter listAdapter;
        ListView list = (ListView) findViewById(R.id.listView);

         listSource = LocalDatabase.getInstance(this).getNewsSource();
         listAdapter = new SimpleCursorAdapter(this,
                R.layout.source_item,
                listSource,
                new String[] {"name","source_text","_id"},
                new int[] { R.id.title, R.id.description});
         list.setAdapter(listAdapter);

        list.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int sourcePosition = i;
                listSource.moveToPosition(sourcePosition);
                String sourceTitle = listSource.getString(listSource.getColumnIndex("name"));
                String sourceDescription = listSource.getString(listSource.getColumnIndex("source_text"));
                int sourceId = listSource.getInt(listSource.getColumnIndex("_id"));

                Intent intent = new Intent(context, NewsListActivity.class);
                intent.putExtra("isNews",true);
                intent.putExtra("sourceId",sourceId);
                intent.putExtra("sourceTitle", sourceTitle);
                intent.putExtra("sourceDescription", sourceDescription);
                context.startActivity(intent);

            }
        });
    }
}
