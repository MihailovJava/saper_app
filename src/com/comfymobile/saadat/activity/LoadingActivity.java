package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.adapter.RSSReader;
import com.comfymobile.saadat.database.LocalDatabase;
import com.comfymobile.saadat.json.RequestSync;
import com.google.analytics.tracking.android.EasyTracker;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;

import java.io.*;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class LoadingActivity extends Activity{
    /**
     * Called when the activity is first created.
     */
    TextView loadText;
    public static Activity activity;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity = this;
        setContentView(R.layout.loading);
        context = this;
        synchronizeDataBase(this);
        loadText = (TextView) findViewById(R.id.loadText);


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
    public void onBackPressed() {
    }
    public static final String ORG = "orgs";
    public static final String CITY = "city";
    public static final String CAT = "category";
    public static final String EVENTS = "afisha";
    public static final String RSS = "rss";
    public static final String RADIO = "radio";
    public static final String COUNTRY = "country";
    public static final String AD = "ad";


    public static final String PROTOCOL = "http://";
    public static final String DOMEN = "saadatru.166.com1.ru";
    public static final String PORT = ":80/";
    public static final String SCRIPT = "db.php?param=";

    private static String getRequestString(String key){
        HttpClient client = new DefaultHttpClient(new BasicHttpParams());
        URI website ;
        HttpGet request = new HttpGet();
        try{
            website = new URI(PROTOCOL+DOMEN+PORT+SCRIPT+key);
            request.setURI(website);
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            InputStream in = entity.getContent();

            //convert response to string
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
            return sb.toString();
        }catch(Exception e){}
        return  null;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private static JSONArray getData(String key)throws Exception{
        try {
            JSONArray jsonArray = new JSONArray(getRequestString(key));
            return jsonArray;
        } catch (Exception e){
        }
        return null;
    }



    public void synchronizeDataBase(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        int lastupdate = preferences.getInt("update",0);
        int today = (int)( System.currentTimeMillis() / 1000 / 60 / 60 / 24);
        if (lastupdate != today && isOnline()){
            new SynchronizeTask(context).execute();
            editor.putInt("update",today);
            editor.commit();
        }else{
            Intent intent = new Intent(context, MenuActivity.class);
            context.startActivity(intent);
            LoadingActivity.activity.finish();
        }
    }

    class SynchronizeTask extends AsyncTask<Void, Integer, Void> {

        JSONArray org;
        JSONArray cit;
        JSONArray cat;
        JSONArray events;
        JSONArray rss;
        JSONArray radio;
        JSONArray country;
        JSONArray ad;


        Context context;

        LocalDatabase database;

        public SynchronizeTask(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                publishProgress(LOAD_START);
                database = LocalDatabase.getInstance(context);
                publishProgress(LOAD_ORG);
                org = getData(ORG);
                publishProgress(LOAD_CITY);
                cit = getData(CITY);
                publishProgress(LOAD_CATS);
                cat = getData(CAT);
                publishProgress(LOAD_EVENTS);
                events = getData(EVENTS);
                rss = getData(RSS);
                radio = getData(RADIO);
                country = getData(COUNTRY);
                ad = getData(AD);

                if (org != null){ database.clearOrganization();
                //update organization
                for (int i=0; i < org.length(); i++){
                    int id = org.getJSONObject(i).getInt("id");
                    String name = org.getJSONObject(i).getString("name");
                    String description = org.getJSONObject(i).getString("description");
                    int id_city = org.getJSONObject(i).getInt("id_city");
                    String address = org.getJSONObject(i).getString("address");
                    String t_number = org.getJSONObject(i).getString("t_number");
                    String site = org.getJSONObject(i).getString("site");
                    int id_category = org.getJSONObject(i).getInt("id_category");
                    String last_mod = org.getJSONObject(i).getString("last_mod");
                    String email = org.getJSONObject(i).getString("email");
                    String lat = org.getJSONObject(i).getString("lat");
                    String lng = org.getJSONObject(i).getString("lng");
                    database.updateOrganization(id,name,description,id_city,address,
                            t_number,site,id_category,last_mod,email,lat,lng);
                }}

                if (cit != null){ database.clearCity();
                //update city
                for (int i=0; i < cit.length(); i++){
                    int id_city = cit.getJSONObject(i).getInt("id_city");
                    String name = cit.getJSONObject(i).getString("name");
                    String last_mod = cit.getJSONObject(i).getString("last_mod");
                    String x = cit.getJSONObject(i).getString("X");
                    String y = cit.getJSONObject(i).getString("Y");
                    int tzone = cit.getJSONObject(i).getInt("tzone");
                    int country_id = cit.getJSONObject(i).getInt("country_id");
                    database.updateCity(id_city,name,last_mod,x,y,tzone,country_id);
                }}

                if (cat != null){ database.clearCategory();
                //update category
                for (int i=0; i < cat.length(); i++){
                    int id_category = cat.getJSONObject(i).getInt("id_category");
                    String name = cat.getJSONObject(i).getString("name");
                    String last_mod = cat.getJSONObject(i).getString("last_mod");
                    database.updateCategory(id_category,name,last_mod);
                }}


                if (events != null){ database.clearEvents();
                //update events
                for (int i = 0 ; i < events.length(); i++){
                    int events_id = events.getJSONObject(i).getInt("id_a");
                    String title = events.getJSONObject(i).getString("name");
                    String text = events.getJSONObject(i).getString("text");
                    String last_mod = events.getJSONObject(i).getString("last_mod");
                    String time = events.getJSONObject(i).getString("time");
                    String oldFormat = "yyyy-MM-dd HH:mm:ss";
                    String newFormat = "dd-MM-yyyy HH:mm";
                    DateFormat format = new SimpleDateFormat(oldFormat);
                    Date oldDate = format.parse(time);
                    format = new SimpleDateFormat(newFormat);
                    time = format.format(oldDate);
                    String city = events.getJSONObject(i).getString("city");
                    String address = events.getJSONObject(i).getString("address");
                    database.updateEvents(events_id,title,text,last_mod,time,city,address);
                }}

                if (rss != null){
                    for (int i = 0; i < rss.length(); i++){
                        int rss_id = rss.getJSONObject(i).getInt("id_rss");
                        String link = rss.getJSONObject(i).getString("link");
                        String country = rss.getJSONObject(i).getString("country");
                        database.updateRSS(rss_id,link,country);
                    }
                }

                if (radio != null){
                    for (int i = 0; i < radio.length(); i++){
                        int radio_id = radio.getJSONObject(i).getInt("id_radio");
                        String link = radio.getJSONObject(i).getString("link");
                        String country = radio.getJSONObject(i).getString("country");
                        String name = radio.getJSONObject(i).getString("name");
                        String img = radio.getJSONObject(i).getString("img");
                        new DownloadImageTask(String.valueOf(radio_id)).execute(img);
                        database.updateRadio(radio_id,link,name,img,country);
                    }
                }

                if (country != null){
                    for (int i = 0; i < country.length(); i++){
                        int id = country.getJSONObject(i).getInt("id_country");
                        String countryS = country.getJSONObject(i).getString("country");
                        String name = country.getJSONObject(i).getString("name");
                        database.updateCountry(id,countryS, name);
                    }
                    String country_id = PreferenceManager.getDefaultSharedPreferences(context).getString("country_id", "1");
                    Cursor country = LocalDatabase.getInstance(context).getCountryName(Integer.valueOf(country_id));
                    Resources res = context.getResources();
                    // Change locale settings in the app.
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.locale = new Locale(country.getString(country.getColumnIndex("country")).toLowerCase());
                    res.updateConfiguration(conf, dm);
                }

                if (ad != null){
                    database.clearAd();
                    for (int i = 0; i < ad.length(); i++){
                        int id = ad.getJSONObject(i).getInt("id_ad");
                        String html = ad.getJSONObject(i).getString("html");
                        int city = ad.getJSONObject(i).getInt("city");
                        database.updateAd(id,html,city);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SynchronizeTask", "no internet access");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            String country_id = PreferenceManager.getDefaultSharedPreferences(context).getString("country_id", "1");
            Cursor country = LocalDatabase.getInstance(context).getCountryName(Integer.valueOf(country_id));
            Cursor rss = database.getRSS(country.getString(country.getColumnIndex("country")));
            String[] rssLink = new String[rss.getCount()];
            for (int i = 0; i < rss.getCount() ; i++){
                rssLink[i] = rss.getString(rss.getColumnIndex("link"));
                rss.moveToNext();
            }
            new RequestSync(context).execute();
            new RSSReader(context,true).execute(rssLink);
        }

        private static final int LOAD_START = 0;
        private static final int LOAD_ORG = 1;
        private static final int LOAD_CITY = 2;
        private static final int LOAD_CATS = 3;
        private static final int LOAD_EVENTS = 5;
        private static final int LOAD_DONE = 100;

        @Override
        protected void onProgressUpdate(Integer... progress){
            super.onProgressUpdate(progress);
            String message = new String();
            switch (progress[0]){
                case LOAD_START:{
                    message = "Подключение...";
                    break;
                }
                case LOAD_ORG:{
                    message = "организации...";
                    break;
                }
                case LOAD_CITY:{
                    message = "города...";
                    break;
                }
                case LOAD_CATS:{
                    message = "категории...";
                    break;
                }
                case LOAD_DONE:{
                    message = "Завершено.";
                    break;
                }
                case LOAD_EVENTS:{
                    message = "афиша...";
                    break;
                }

            }
            loadText.setText("Загрузка - "+message.toString());
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        String  sourceId;


        public DownloadImageTask(String sourceId) {
            this.sourceId = sourceId;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outHeight = 48;
            options.outWidth = 48;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in,null,options);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            String path = context.getExternalCacheDir()+"/"+
                    sourceId+
                    ".png";
            if (result == null)
                result = BitmapFactory.decodeResource(getResources(),R.drawable.btn_alarm_enable);
            try {
                FileOutputStream  out = new FileOutputStream(path);
                result.compress(Bitmap.CompressFormat.PNG, 90, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
