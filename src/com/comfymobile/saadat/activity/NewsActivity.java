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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news);
        context = this;

        initUI();
    }
    void initUI(){
        ListView list = (ListView) findViewById(R.id.listView);
        listSource = LocalDatabase.getInstance(this).getNews(-1);
        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this,
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
                context.startActivity(intent);

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
