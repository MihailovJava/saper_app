package com.comfymobile.saadat.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.adapter.SimplePagerAdapter;
import com.comfymobile.saadat.database.LocalDatabase;
import com.comfymobile.saadat.service.SaadatService;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class OrganizationListActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

    private static final int LIST = 0;
    private static final int MAP = 1;

    int currentCity;
    int currentCategory;
    Cursor listSource;
    Context context;
    String categoryName;
    Cursor citySource;
    ListView list;
    ViewPager pager;
    SearchView searchView;
    MenuItem itemSarch;
    List<View>  listOfViews;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        currentCity = intent.getIntExtra("cityID",-1);
        categoryName = intent.getStringExtra("categoryName");
        currentCategory = intent.getIntExtra("categoryID",-1);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(categoryName);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        setContentView(R.layout.org_pager);
        context = this;

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.org_list_ab,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        itemSarch = menu.findItem(R.id.action_search);
        searchView = (SearchView) itemSarch.getActionView();


        if (null != searchView ){

            ImageView imageView = (ImageView) searchView.findViewById(R.id.abs__search_mag_icon);
            imageView.setImageResource(R.drawable.ab_search);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
        {
            public boolean onQueryTextChange(String newText){
                if (newText != null && newText.length() > 2 ){
                    listSource = LocalDatabase.getInstance(context).getListSourceByQuery(currentCity,currentCategory,newText);
                    SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(context,
                            R.layout.list_item,
                            listSource,
                            new String[] {"name","address","_id"},
                            new int[] { R.id.name, R.id.address});
                    list.setAdapter(listAdapter);
                } else {
                    listSource = LocalDatabase.getInstance(context).getListSource(currentCity,currentCategory);
                    SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(context,
                            R.layout.list_item,
                            listSource,
                            new String[] {"name","address","_id"},
                            new int[] { R.id.name, R.id.address});
                    list.setAdapter(listAdapter);
                }

                return true;
            }

            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                //  adapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
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


    View mapLayout;
    View listLayout;
    void initUI(){
        pager = (ViewPager) findViewById(R.id.pager);
        ViewPager.SimpleOnPageChangeListener viewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Find the ViewPager Position
                getSupportActionBar().setSelectedNavigationItem(position);
            }
        };

        pager.setOnPageChangeListener(viewPagerListener);


        listOfViews = new ArrayList<View>();
        mapLayout = View.inflate(context,R.layout.org_map,null);
        listLayout = View.inflate(context,R.layout.orglist,null);

        pager.addView(listLayout);
        pager.addView(mapLayout);

        listOfViews.add(listLayout);
        listOfViews.add(mapLayout);

        pager.setAdapter(new SimplePagerAdapter(listOfViews));

        ActionBar.Tab listTab = getSupportActionBar().newTab();
        listTab.setText(getString(R.string.listTab));
        listTab.setTag(LIST);
        listTab.setTabListener(this);
        getSupportActionBar().addTab(listTab);

        ActionBar.Tab mapTab = getSupportActionBar().newTab();
        mapTab.setText(getString(R.string.mapTab));
        mapTab.setTag(MAP);
        mapTab.setTabListener(this);
        getSupportActionBar().addTab(mapTab);

        getSupportActionBar().setSelectedNavigationItem(0);

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


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        pager.setCurrentItem(tab.getPosition());
        switch (Integer.valueOf(tab.getTag().toString())){
            case LIST:
                mapLayout.setVisibility(View.GONE);
                if (itemSarch != null)
                    itemSarch.setVisible(true);

                list = (ListView) listOfViews.get(LIST).findViewById(R.id.listView);
                listSource = LocalDatabase.getInstance(this).getListSource(currentCity,currentCategory);
                SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this,
                        R.layout.list_item,
                        listSource,
                        new String[] {"name","address","_id"},
                        new int[] { R.id.name, R.id.address});
                list.setAdapter(listAdapter);
                list.setOnItemClickListener(new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        int orgPosition = i;
                        listSource.moveToPosition(orgPosition);
                        int orgID = listSource.getInt(listSource.getColumnIndex("_id"));
                        Intent intent = new Intent(context, DetalOrganizationActivity.class);
                        intent.putExtra("id", orgID);

                        String orgName = listSource.getString(LocalDatabase.ORG_NAME_IND);
                        citySource = LocalDatabase.getInstance(context).getCitySource(currentCity);
                        String cityName = citySource.getString(LocalDatabase.CITY_NAME_IND);
                        EasyTracker.getInstance(context).send(MapBuilder
                                .createEvent("ui_action", "organizationSelect", "Город = " + cityName + " Организация = " + orgName, null)
                                .build());

                        context.startActivity(intent);
                    }
                });



            break;
            case MAP:
                mapLayout.setVisibility(View.VISIBLE);
                if (itemSarch != null){
                    itemSarch.setVisible(false);
                    itemSarch.collapseActionView();
                }

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                GoogleMap map = ((SupportMapFragment) fragment ).getMap();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                int city_id = Integer.valueOf(preferences.getString("city_id", SaadatService.MOSCOW_ID));

                double city_lat = SaadatService.getCityLat(city_id,this);
                double city_lng = SaadatService.getCityLng(city_id,this);
                int zoom = 10 ;
                LatLng city = new LatLng(city_lat, city_lng);
                map.clear();
                map.setMyLocationEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(city, zoom));
                listSource.moveToFirst();
               // listSource = LocalDatabase.getInstance(this).getListSource(currentCity,currentCategory);
                for (int i = 0 ; i < listSource.getCount(); i++){
                    int id = listSource.getInt(listSource.getColumnIndex("_id"));

                    Cursor orgsource = LocalDatabase.getInstance(this).getDetal(id);
                    String orgLat = orgsource.getString(orgsource.getColumnIndex("lat"));
                    String orgLng = orgsource.getString(orgsource.getColumnIndex("lng"));
                    String orgName = orgsource.getString(orgsource.getColumnIndex("org_name"));
                    String orgAdress = orgsource.getString(orgsource.getColumnIndex("address"));
                    int orgIdCat = orgsource.getInt(orgsource.getColumnIndex("id_category"));
                    Double org_lat = DetalOrganizationActivity.getLatFromString(orgLat);
                    Double org_lng = DetalOrganizationActivity.getLatFromString(orgLng);
                    if (org_lat != null){
                        LatLng org = new LatLng(org_lat, org_lng);
                        map.addMarker(new MarkerOptions()
                                .title(orgName).position(org)
                                .snippet(orgAdress)
                                .icon(MapActivity.getIcon(orgIdCat)));
                    }
                    listSource.moveToNext();
                    map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                           String id =  marker.getId();
                           id = id.replace("m","");
                           int intID = Integer.valueOf(id);
                           listSource.moveToPosition(intID);

                           int orgID = listSource.getInt(listSource.getColumnIndex("_id"));
                           Intent intent = new Intent(context, DetalOrganizationActivity.class);
                           intent.putExtra("id", orgID);

                           String orgName = listSource.getString(LocalDatabase.ORG_NAME_IND);
                           citySource = LocalDatabase.getInstance(context).getCitySource(currentCity);
                           String cityName = citySource.getString(LocalDatabase.CITY_NAME_IND);
                           EasyTracker.getInstance(context).send(MapBuilder
                                   .createEvent("ui_action", "organizationSelect", "Город = " + cityName + " Организация = " + orgName, null)
                                   .build());

                           context.startActivity(intent);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
