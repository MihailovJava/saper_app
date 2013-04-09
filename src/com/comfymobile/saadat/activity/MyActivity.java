package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.adapter.ListAdapter;
import com.comfymobile.saadat.client.JSONClient;


public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new JSONClient().synchronizeDataBase(this);
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(new ListAdapter(this));
    }
}
