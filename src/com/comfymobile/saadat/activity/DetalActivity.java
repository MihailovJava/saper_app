package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.client.JSONClient;
import com.comfymobile.saadat.database.LocalDatabase;


public class DetalActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    TextView name;
    TextView address;
    TextView tn;
    TextView email;
    TextView site;
    TextView description;
    TextView category;

    Cursor sourceOrganization;

    int currentID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detal);
        Intent intent = getIntent();
        currentID = intent.getIntExtra("id",-1);
        initUI();
    }
    void initUI(){
        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        tn = (TextView) findViewById(R.id.tn);
        email = (TextView) findViewById(R.id.email);
        site = (TextView) findViewById(R.id.site);
        description = (TextView) findViewById(R.id.description);
        category = (TextView) findViewById(R.id.category);

        sourceOrganization = LocalDatabase.getInstance(this).getDetal(currentID);

        name.setText(sourceOrganization.getString(sourceOrganization.getColumnIndex("name")));
        address.setText(sourceOrganization.getString(sourceOrganization.getColumnIndex("address")));
        tn.setText(sourceOrganization.getString(sourceOrganization.getColumnIndex("t_number")));
        name.setText(sourceOrganization.getString(sourceOrganization.getColumnIndex("name")));
        site.setText(sourceOrganization.getString(sourceOrganization.getColumnIndex("site")));
        description.setText(sourceOrganization.getString(sourceOrganization.getColumnIndex("description")));
        //category.setText(String.valueOf(sourceOrganization.getInt(sourceOrganization.getColumnIndex("id_category")));
    }
}
