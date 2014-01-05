package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
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


public class LoadingActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    TextView loadText;
    public static Activity activity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity = this;
        setContentView(R.layout.loading);
    }

    @Override
    public void onResume(){
        super.onResume();
        synchronizeDataBase(this);
        loadText = (TextView) findViewById(R.id.loadText);
    }

    @Override
    public void onBackPressed() {
    }
    public static final String ORG = "organization";
    public static final String CITY = "city";
    public static final String CAT = "category";
    public static final String NEWS = "news";
    public static final String EVENTS = "afisha";
    public static final String NAMAS = "namaz";
    public static final String NEWS_SOURCE = "news_source";

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

    private static JSONArray getData(String key)throws Exception{
        try {
            JSONArray jsonArray = new JSONArray(getRequestString(key));
            return jsonArray;
        } catch (Exception e){
        }
        return null;
    }

    public void synchronizeDataBase(Context context){
        new SynchronizeTask(context).execute();
    }

    class SynchronizeTask extends AsyncTask<Void, Integer, Void> {

        JSONArray org;
        JSONArray cit;
        JSONArray cat;
        JSONArray news;
        JSONArray events;
        JSONArray namas;
        JSONArray news_source;


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
                publishProgress(LOAD_NEWS);
                news = getData(NEWS);
                publishProgress(LOAD_EVENTS);
                events = getData(EVENTS);
                publishProgress(LOAD_NAMAS);
                namas = getData(NAMAS);
                publishProgress(LOAD_NEWS_SOURCE);
                news_source = getData(NEWS_SOURCE);
                publishProgress(LOAD_DONE);

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
                    database.updateOrganization(id,name,description,id_city,address,t_number,site,id_category,last_mod,email);
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

                if (news != null){ database.clearNews();
                //update news
                for (int i=0; i < news.length(); i++){
                    int news_id = news.getJSONObject(i).getInt("id_news");
                    String title = news.getJSONObject(i).getString("title");
                    String text = news.getJSONObject(i).getString("text");
                    int id_source = news.getJSONObject(i).getInt("id_source");
                    String last_mod = news.getJSONObject(i).getString("last_mod");
                    database.updateNews(news_id,title,text,last_mod,id_source);
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
                if (namas != null){ database.clearNamas();
                //update namas
                for (int i = 0 ; i < namas.length(); i++){
                    int namas_id = namas.getJSONObject(i).getInt("id_namaz");
                    String date = namas.getJSONObject(i).getString("date");
                    String fajr = namas.getJSONObject(i).getString("n1");
                    String sunrise = namas.getJSONObject(i).getString("n2");
                    String dhuhr = namas.getJSONObject(i).getString("n3");
                    String asr = namas.getJSONObject(i).getString("n4");
                    String maghrib = namas.getJSONObject(i).getString("n5");
                    String isha = namas.getJSONObject(i).getString("n6");
                    int city_id = namas.getJSONObject(i).getInt("id_city");
                    database.updateNamas(namas_id,date,fajr,sunrise,dhuhr,asr,maghrib,isha,city_id);
                }}

                if (news_source != null){ database.clearNewsSource();
                //update news_source
                for (int i=0; i < news_source.length(); i++){
                    int id_source = news_source.getJSONObject(i).getInt("id_source");
                    String name = news_source.getJSONObject(i).getString("name");
                    String text = news_source.getJSONObject(i).getString("text");
                    String last_mod = news_source.getJSONObject(i).getString("last_mod");
                    database.updateNewsSource(id_source,name,text,last_mod);
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
            Intent intent = new Intent(context, MenuActivity.class);
            context.startActivity(intent);
            LoadingActivity.activity.finish();
        }

        private static final int LOAD_START = 0;
        private static final int LOAD_ORG = 1;
        private static final int LOAD_CITY = 2;
        private static final int LOAD_CATS = 3;
        private static final int LOAD_NEWS = 4;
        private static final int LOAD_EVENTS = 5;
        private static final int LOAD_NAMAS = 6;
        private static final int LOAD_NEWS_SOURCE = 7;
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
                case LOAD_NAMAS:{
                    message = "время...";
                    break;
                }
                case LOAD_NEWS_SOURCE:{
                    message = "источники...";
                    break;
                }
            }
            loadText.setText("Загрузка - "+message.toString());
        }
    }
}
