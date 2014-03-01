package com.comfymobile.saadat.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.comfymobile.saadat.service.SaadatService;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

/**
 * Created by Nixy on 06.02.14.
 */
public class MapActivity extends SherlockFragmentActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.ab_map_item);
        // Get a handle to the Map Fragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        GoogleMap map = ((SupportMapFragment) fragment ).getMap();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int city_id = Integer.valueOf(preferences.getString("city_id", SaadatService.MOSCOW_ID));

        double city_lat = SaadatService.getCityLat(city_id,this);
        double city_lng = SaadatService.getCityLng(city_id,this);


        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("org_lat",city_lat );
        double lng = intent.getDoubleExtra("org_lng",city_lng);
        int id_cat = intent.getIntExtra("id_cat", -1 );
        String name = this.getString(R.string.org_not_found);
        String adress = "";
        if (lat != city_lat){
            adress = intent.getStringExtra("org_adress");
            name = intent.getStringExtra("name");
        }
        int zoom = adress.equals("") ? 10 : 18;
        LatLng org = new LatLng(lat, lng);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(org, zoom));

        map.addMarker(new MarkerOptions()
                .title(name).position(org)
                .snippet(adress)
                .icon(getIcon(id_cat)));


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

    public static final int CAFE = 1;
    public static final int BANK = 3;
    public static final int CSHOP = 4;
    public static final int ISHOP = 5;
    public static final int FSHOP = 6;
    public static final int NEWS = 7;
    public static final int FOOD = 8;
    public static final int MOSQUE = 9;
    public static final int PRAY = 10;
    public static final int STUDY = 11;
    public static final int CHARITY = 12;
    public static final int TRIP = 13;
    public static final int HADJ = 14;
    public static final int SHOP = 15;
    public static final int ORG = 16;

    public static BitmapDescriptor getIcon(int type) {
        switch (type){
            case CAFE: return BitmapDescriptorFactory.fromResource(R.drawable.markers_04);
            case BANK: return BitmapDescriptorFactory.fromResource(R.drawable.markers_06);
            case CSHOP: return BitmapDescriptorFactory.fromResource(R.drawable.markers_05);
            case ISHOP: return BitmapDescriptorFactory.fromResource(R.drawable.markers_03);
            case FSHOP: return BitmapDescriptorFactory.fromResource(R.drawable.markers_05);
            case NEWS: return BitmapDescriptorFactory.fromResource(R.drawable.markers_03);
            case FOOD: return BitmapDescriptorFactory.fromResource(R.drawable.markers_04);
            case MOSQUE: return BitmapDescriptorFactory.fromResource(R.drawable.markers_02);
            case PRAY: return BitmapDescriptorFactory.fromResource(R.drawable.markers_02);
            case STUDY: return BitmapDescriptorFactory.fromResource(R.drawable.markers_07);
            case CHARITY: return BitmapDescriptorFactory.fromResource(R.drawable.markers_07);
            case TRIP: return BitmapDescriptorFactory.fromResource(R.drawable.markers_08);
            case HADJ: return BitmapDescriptorFactory.fromResource(R.drawable.markers_08);
            case SHOP: return BitmapDescriptorFactory.fromResource(R.drawable.markers_05);
            case ORG: return BitmapDescriptorFactory.fromResource(R.drawable.markers_06);
            default: return BitmapDescriptorFactory.fromResource(R.drawable.markers_01);
        }
    }


}
