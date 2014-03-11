package com.comfymobile.saadat.json;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Author Grinch
 * Date: 11.03.14
 * Time: 19:19
 */
public class RequestSync extends AsyncTask<Void,Void,Void>{
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static final int TYPE_ORGANIZATIONS = 0;
    public static final int TYPE_EVENTS = 1;

    //SERVER SETTINGS
    public static final String ROOT = "http://saadat.ru/";
    public static final String SCRIPT_NAME = "requests.php";
    public static final String KEY_JSON = "json";

    public RequestSync(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (sendRequests(context)){
            editor.putLong("sync",new Date().getTime()/1000);
            editor.commit();
        }

        return null;
    }

    public boolean sendRequests(Context context){
        Log.d("Saadat", "Start sending requests");
        boolean result = false;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ROOT+SCRIPT_NAME);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(KEY_JSON, getRequests()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            if (response.getEntity().getContentLength() > 0){
                StringBuilder sb = new StringBuilder();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                if (sb.toString().contains("OK"))
                    result = true;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getRequests() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        List<Object> list = new LinkedList<Object>();
        long last_sync = preferences.getLong("sync", 0);
        Cursor requests = LocalDatabase.getInstance(context).getRequests(-1);
        while (!requests.isAfterLast()){
            long modification = requests.getInt(requests.getColumnIndex("modification"));
            if (modification > last_sync) {
                Log.d("Saadat",String.valueOf(modification)+">"+String.valueOf(last_sync)+" +");
                String json = requests.getString(requests.getColumnIndex("json"));
                list.add(new Gson().fromJson(json,Object.class));
            }else{
                Log.d("Saadat",String.valueOf(modification)+">"+String.valueOf(last_sync)+" -");
            }
            requests.moveToNext();
        }
        Log.d("Saadat","Result json: "+new Gson().toJson(list));
        return new Gson().toJson(list);
    }

   /* Object getObject(String json, int type){
        switch (type){
            case TYPE_ORGANIZATIONS:
                return new Gson().fromJson(json,OrganizationRequest.class);
            case TYPE_EVENTS:
                return new Gson().fromJson(json,EventRequest.class);
            default:
                return new Gson().fromJson(json,Object.class);
        }
    }*/
}
