package com.comfymobile.saadat.activity;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.comfymobile.saadat.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Nixy on 06.02.14.
 */
public class MapActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);


        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setLogo(R.drawable.ab_back);
        ab.setTitle(R.string.ab_map_item);
        // Get a handle to the Map Fragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        GoogleMap map = ((SupportMapFragment) fragment ).getMap();

        LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }

}
