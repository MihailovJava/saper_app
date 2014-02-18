package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.adapter.RSSReader;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;


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
                    database.updateCity(id_city,name,last_mod,x,y,tzone);
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
                    String city = events.getJSONObject(i).getString("city");
                    String address = events.getJSONObject(i).getString("address");
                    database.updateEvents(events_id,title,text,last_mod,time,city,address);
                }}
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SynchronizeTask", "no internet access");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            new RSSReader(context,true).execute(new String[]{
                    "http://www.islamrf.ru/rss/",
                    "http://islam-today.ru/rss/",
                    "http://www.muslimeco.ru/rss.php"
            });
            publishProgress(LOAD_NEWS);
        }

        private static final int LOAD_START = 0;
        private static final int LOAD_ORG = 1;
        private static final int LOAD_CITY = 2;
        private static final int LOAD_CATS = 3;
        private static final int LOAD_NEWS = 4;
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
                case LOAD_NEWS:{
                    message = "новости...";
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
}
