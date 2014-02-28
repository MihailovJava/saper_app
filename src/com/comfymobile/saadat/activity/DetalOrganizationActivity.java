package com.comfymobile.saadat.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;


public class DetalOrganizationActivity extends SherlockActivity {

    WebView text;
    TextView category;
    Context context;
    Cursor sourceOrganization;
    int currentID;
    String orgName;
    String orgLat;
    String orgLng;
    int orgIdCat;
    String orgAdress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.detal);
        Intent intent = getIntent();
        currentID = intent.getIntExtra("id",-1);
        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.org_detal_ab,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //needs import android.view.MenuItem;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.ab_map_button:
                Intent intent = new Intent(context,MapActivity.class);
                intent.putExtra("name",orgName);
                intent.putExtra("org_lat",getLatFromString(orgLat));
                intent.putExtra("org_lng",getLngFromString(orgLng));
                intent.putExtra("id_cat",orgIdCat);
                intent.putExtra("org_adress",orgAdress);
                context.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Double getLatFromString(String lat){
        if (lat != null && !lat.equals("null")){
            lat = lat.replaceAll("[^\\p{ASCII}]", "");
            return Double.valueOf(lat);
        }
        return null;
    }

    public static Double getLngFromString(String lng){
        if (lng != null && !lng.equals("null")){
            lng = lng.replaceAll("[^\\p{ASCII}]", "");
            return Double.valueOf(lng);
        }
        return null;
    }


    void initUI(){

        sourceOrganization = LocalDatabase.getInstance(this).getDetal(currentID);

        StringBuilder info = new StringBuilder();
        info.append("<b>");
        orgName = sourceOrganization.getString(sourceOrganization.getColumnIndex("org_name"));
        orgAdress = sourceOrganization.getString(sourceOrganization.getColumnIndex("address"));
        String orgTNumber = sourceOrganization.getString(sourceOrganization.getColumnIndex("t_number"));
        String orgEmail = sourceOrganization.getString(sourceOrganization.getColumnIndex("email"));
        String orgSite = sourceOrganization.getString(sourceOrganization.getColumnIndex("site"));
        String orgDescription = sourceOrganization.getString(sourceOrganization.getColumnIndex("description"));
        String orgCatName = sourceOrganization.getString(sourceOrganization.getColumnIndex("cat_name"));
        orgLat = sourceOrganization.getString(sourceOrganization.getColumnIndex("lat"));
        orgLng = sourceOrganization.getString(sourceOrganization.getColumnIndex("lng"));
        orgIdCat = sourceOrganization.getInt(sourceOrganization.getColumnIndex("id_category"));
        info.append(orgName);
        info.append("</b><br>");
        info.append("<b>Адрес:</b> ");
        info.append(orgAdress);
        info.append("<br><b>Телефон:</b> ");
        info.append(orgTNumber);
        info.append("<br><b>E-mail:</b> ");
        info.append("<a href=\"mailto:"+orgEmail+"\">"+orgEmail+"</a> ");
        info.append("<br><b>Сайт:</b> ");
        info.append("<a href=\""+orgSite+"\">"+orgSite+"</a>");
        info.append("<br><b><p align=\"justify\">Дополнительная информация:</b><br>");
        info.append(orgDescription);
        info.append("</p>");

        text = (WebView) findViewById(R.id.text);
        text.loadDataWithBaseURL(null, info.toString(), "text/html", "utf-8", null);

        category = (TextView) findViewById(R.id.category);
        category.setText("Категория: "+orgCatName);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(orgName);
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
