package com.comfymobile.saadat.client;


import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import com.comfymobile.saadat.database.LocalDatabase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;



/**
 * User: Nixy
 * Date: 04.04.13
 * Time: 14:36
 */
public class JSONClient {
    public static final String ORG = "organization";
    public static final String CITY = "city";
    public static final String CAT = "category";
    public static final String PROTOCOL = "http://";
    public static final String DOMEN = "mai-dormitory.ru";
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
         new SynchronizeTask().execute(context);
    }


    class SynchronizeTask extends AsyncTask<Context, Void, Void> {

        JSONArray org;
        JSONArray cit;
        JSONArray cat;
        LocalDatabase database;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Context... params) {
            try {
                database = new LocalDatabase(params[0]);
                org = JSONClient.getData(JSONClient.ORG);
                cit = JSONClient.getData(JSONClient.CITY);
                cat = JSONClient.getData(JSONClient.CAT);

                database.clearDatabase();
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
                    database.updateOrganization(id,name,description,id_city,address,t_number,site,id_category,last_mod);
                }
                //update city
                for (int i=0; i < cit.length(); i++){
                    int id_city = cit.getJSONObject(i).getInt("id_city");
                    String name = cit.getJSONObject(i).getString("name");
                    String last_mod = cit.getJSONObject(i).getString("last_mod");
                    database.updateCity(id_city,name,last_mod);
                }
                //update category
                for (int i=0; i < cat.length(); i++){
                    int id_category = cat.getJSONObject(i).getInt("id_category");
                    String name = cat.getJSONObject(i).getString("name");
                    String last_mod = cat.getJSONObject(i).getString("last_mod");
                    database.updateCategory(id_category,name,last_mod);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
