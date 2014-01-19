package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.comfymobile.saadat.R;

/**
 * User: Nixy
 * Date: 29.04.13
 * Time: 21:26
 */
public class MenuActivity extends Activity {

    private Button news_button;
    private Button namaz_button;
    private Button afisha_button;
    private Button radio_button;
    private Button catalog_button;
    private Button info_button;

    private Context context;

    private void initUI(){
        news_button = (Button) findViewById(R.id.news_button);
        namaz_button = (Button) findViewById(R.id.namaz_button);
        afisha_button = (Button) findViewById(R.id.afisha_button);
        radio_button = (Button) findViewById(R.id.radio_button);
        catalog_button = (Button) findViewById(R.id.list_button);
        info_button =   (Button) findViewById(R.id.info_button);

        news_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,SourceListActivity.class);
                context.startActivity(intent);
            }
        });

        namaz_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,NamasActivity.class);
                context.startActivity(intent);
            }
        });

        afisha_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,NewsListActivity.class);
                intent.putExtra("news",false);
                context.startActivity(intent);
            }
        });

        radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,RadioActivity.class);
                context.startActivity(intent);
            }
        });

        catalog_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(context,SearchActivity.class);
               context.startActivity(intent);
            }
        });

        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,InfoActivity.class);
                context.startActivity(intent);
            }
        });
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        setContentView(R.layout.menu);
        initUI();
    }
}


