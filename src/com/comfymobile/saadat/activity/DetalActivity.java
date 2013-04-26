package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;


public class DetalActivity extends Activity {

    TextView name;
    TextView address;
    TextView tn;
    TextView site;
    TextView description;
    TextView category;
    TextView email;
    Button back;
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
        site = (TextView) findViewById(R.id.site);
        description = (TextView) findViewById(R.id.description);
        category = (TextView) findViewById(R.id.category);
        email = (TextView) findViewById(R.id.email);
        back = (Button) findViewById(R.id.back_button);
        sourceOrganization = LocalDatabase.getInstance(this).getDetal(currentID);

        name.setText("Name: "+sourceOrganization.getString(sourceOrganization.getColumnIndex("org_name")));
        address.setText("Address: "+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.address")));
        tn.setText("T/N: "+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.t_number")));
        site.setText("Site: "+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.site")));
        description.setText("Description: "+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.description")));
        category.setText("Category: "+sourceOrganization.getString(sourceOrganization.getColumnIndex("cat_name")));
        email.setText("E-mail: "+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.email")));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
