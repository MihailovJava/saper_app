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
import com.google.analytics.tracking.android.EasyTracker;


public class DetalActivity extends Activity {

    TextView text;
    TextView category;
    Button back;
    Cursor sourceOrganization;
    int currentID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.detal);
        Intent intent = getIntent();
        currentID = intent.getIntExtra("id",-1);
        initUI();
    }
    void initUI(){

        sourceOrganization = LocalDatabase.getInstance(this).getDetal(currentID);

        StringBuilder info = new StringBuilder();
        info.append("<b>");
        info.append(sourceOrganization.getString(sourceOrganization.getColumnIndex("org_name")));
        info.append("</b><br>");
        info.append("<b>Адрес:</b> ");
        info.append(sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.address")));
        info.append("<br><b>Телефон:</b> ");
        info.append(sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.t_number")));
        info.append("<br><b>E-mail:</b> ");
        info.append("<a href=\"mailto:"+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.email"))+"\">"+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.email"))+"</a> ");
        info.append("<br><b>Сайт:</b> ");
        info.append("<a href=\""+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.site"))+"\">"+sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.site"))+"</a>");
        info.append("<br><b>Дополнительная информация:</b><br>");
        info.append(sourceOrganization.getString(sourceOrganization.getColumnIndex("organization.description")));

        text = (TextView) findViewById(R.id.text);
        text.setText(Html.fromHtml(info.toString()));

        category = (TextView) findViewById(R.id.category);
        category.setText("Категория: "+sourceOrganization.getString(sourceOrganization.getColumnIndex("cat_name")));

        back = (Button) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
