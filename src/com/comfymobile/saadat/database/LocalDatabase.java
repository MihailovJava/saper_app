package com.comfymobile.saadat.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.comfymobile.saadat.adapter.PrayTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * User: Nixy
 * Date: 06.04.13
 * Time: 16:24
 */
public class LocalDatabase{
    DatabaseHelper helper;
    SQLiteDatabase database;
    Context context;
    public static final int CATEGORY_NAME_IND = 1;
    public static final int CITY_NAME_IND = 1;
    public static final int ORG_NAME_IND = 1;
    public static final int NEWS_NAME_IND = 0;

    private static LocalDatabase localDatabaseInstance;

    private LocalDatabase(Context context){
        helper = new DatabaseHelper(context);
        database = helper.getWritableDatabase();
    }

    public boolean isLocked(){
        return database.isDbLockedByOtherThreads();
    }

    public  static LocalDatabase getInstance(Context context) {
        if (localDatabaseInstance == null){
            localDatabaseInstance = new LocalDatabase(context);
        }
        return localDatabaseInstance;
    }

    public void clearNewsSource(){
        database.execSQL("DROP TABLE newssource");
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_NEWSSOURCE);
    }

    public void clearOrganization(){
        database.execSQL("DROP TABLE organization");
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_ORGANIZATION);
    }

    public void clearCity(){
        database.execSQL("DROP TABLE city");
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_CITY);
    }

    public void clearCategory(){
        database.execSQL("DROP TABLE category");
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_CATEGORY);
    }
    public void clearNews(){
        database.execSQL("DROP TABLE news");
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_NEWS);
    }

    public void clearEvents(){
        database.execSQL("DROP TABLE events");
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_EVENTS);
    }

    public void clearNamas(){
        database.execSQL("Drop table namas");
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_NAMAS);
    }

    public void clearNewsState(){
        database.execSQL("Drop table newsstate");
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_NEWS_STATE);
    }



    public void updateOrganization(int id, String name, String description, int id_city,
                                        String address, String t_number, String site,
                                        int id_category,String last_mod,String email,
                                        String lat, String lng){
        String query = "insert or replace into organization " +
                "(_id, name, description, id_city, address, t_number, site, id_category, last_mod, email, lat, lng)" +
                " values (?,?,?,?,?,?,?,?,?,?,?,?)";

        database.execSQL(query,new String[]{String.valueOf(id),name,description,String.valueOf(id_city),
                address,t_number,site,String.valueOf(id_category),last_mod,email,lat,lng});
    }

    public void updateCity(int id_city, String name, String last_mod, String x, String y, int tzone, int country_id){
        String query = "insert or replace into city (_id, name, last_mod, x, y, tzone, country_id) values (?,?,?,?,?,?,?)";
        database.execSQL(query,new String[]{String.valueOf(id_city),name,last_mod,x,y,String.valueOf(tzone),String.valueOf(country_id)});
    }
    public void updateCategory(int id_category, String name, String last_mod){
        String query = "insert or replace into category (_id, name, last_mod) values (?,?,?)";
        database.execSQL(query, new String[]{String.valueOf(id_category),name,last_mod});
    }

    public void updateNews(String title, String text, String last_mod, int id_source, String url){
        String query = "insert or replace into news (  title , news_text, last_mod, id_source, url) values (?,?,?,?,?)";
        database.execSQL(query,new  String[]{title,text,last_mod,String.valueOf(id_source),url});
    }

    public void updateNewsSource(int id_source,String name, String source_text, String last_mod){
        String query = "insert or replace into newssource ( _id , name , source_text, last_mod) values (?,?,?,?)";
        database.execSQL(query,new  String[]{String.valueOf(id_source),name,source_text,last_mod});
    }

    public void updateEvents(int events_id,String title, String text,String last_mod,String time, String city, String address){
        String query = "insert or replace into events ( _id , title , events_text, last_mod, time, city, address) values (?,?,?,?,?,?,?)";
        database.execSQL(query,new  String[]{String.valueOf(events_id),title,text,last_mod, time, city, address});
    }

    public void updateNamasTime(int id, String time){
        String query = " update namas set time = ? WHERE _id = ?";
        database.execSQL(query,new  String[]{time,String.valueOf(id)});
    }

    public void dropNamasMiss(){
        String query = " update namas set miss = 0";
        database.execSQL(query);
    }

    public void updateNamasMiss(int id, int miss){
        String query = " update namas set miss = ? WHERE _id = ?";
        database.execSQL(query,new  String[]{String.valueOf(miss),String.valueOf(id)});
    }

    public void updateNamasFlag(int id, int flag){
        String query = " update namas set flag = ? WHERE _id = ?";
        database.execSQL(query,new  String[]{String.valueOf(flag),String.valueOf(id)});
    }

    public void updateNewsState(String title,String text){
        String query = "insert or replace into newsstate ( news_text , title ) values (?,?)";
        database.execSQL(query,new String[]{text,title});
    }

    public void addRequestOrg(String json,long modification){
        String query = "insert into req_org ( json , modification ) values (?,?)";
        database.execSQL(query,new String[]{json,String.valueOf(modification)});
    }

    public void updateRSS(int id_rss, String link, String country){
        String query = "insert or replace into rss (_id, link, country) values (?,?,?)";
        database.execSQL(query, new String[]{String.valueOf(id_rss),link,country});
    }

    public void updateRadio(int id_radio, String link,String name, String img, String country){
        String query = "insert or replace into radio (_id, link,name ,img , country) values (?,?,?,?,?)";
        database.execSQL(query, new String[]{String.valueOf(id_radio),link,name,img,country});
    }

    public void updateCountry(int id ,String country, String name) {
        String query = "insert or replace into country (_id, country, name) values (?,?,?)";
        database.execSQL(query, new String[]{String.valueOf(id),country,name});
    }



    public Cursor getListSource(int city,int category){
        String args = new String();
        if (city != -1 || category != -1){
            if (city != -1 && category != -1) args = "WHERE id_city="+String.valueOf(city)+" AND id_category="+String.valueOf(category);
            if (city != -1 && category == -1) args = "WHERE id_city="+String.valueOf(city);
            if (city == -1 && category != -1) args = "WHERE id_category="+String.valueOf(category);
        }
        String query = "SELECT _id, name, address FROM organization "+args;

        Cursor cursor = database.rawQuery(query,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public Cursor getNewsSource(){
        String query = "SELECT _id , name, source_text FROM newssource";
        Cursor cursor = database.rawQuery(query,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getCategorySource(int cityID){
        String args[] = null;
        String query = "SELECT _id , name FROM category";

        if (cityID > 0){
            query += " WHERE _id IN (SELECT id_category " +
                    "FROM organization WHERE id_city= ? );";
            args = new String[]{String.valueOf(cityID)};
        }
        Cursor cursor = database.rawQuery(query,args);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getCitySource(int id){
        String args[] = null;
          String query = "SELECT _id , name, country_id FROM city ";

        if (id > 0){
            query += " WHERE _id = ?";
            args = new String[]{String.valueOf(id)};
        }
        Cursor cursor = database.rawQuery(query,args);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getCitySourceByCountry(int country_id){
        String query = "SELECT _id , name, x, y, tzone, country_id FROM city WHERE country_id = ?";
        Cursor cursor = database.rawQuery(query,new String[]{String.valueOf(country_id)});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }



    public Cursor getCity(int id){
        String query = "SELECT _id , name, x , y, tzone, country_id FROM city WHERE _id = ?";
        Cursor cursor = database.rawQuery(query,new String[]{String.valueOf(id)});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }



    public Cursor getDetal(int id){
        String query = "SELECT " +
                "T1.name as org_name, " +
                "T1.description, " +
                "T1.address, " +
                "T1.t_number, " +
                "T1.site, " +
                "T1.id_category, " +
                "T1.id_city, " +
                "T1.last_mod, " +
                "T1.email, " +
                "T1.lat," +
                "T1.lng," +
                "T2.name as cat_name, " +
                "T2._id " +
                "FROM organization as T1 " +
                "LEFT JOIN category as T2 " +
                "ON T1.id_category = T2._id " +
                "WHERE T1._id="+String.valueOf(id);
        Cursor cursor = database.rawQuery(query,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getNews(int id, int id_s){
        String args[] = null;
        String query = "SELECT title , news_text, _id, last_mod, id_source, url FROM news WHERE id_source = "+String.valueOf(id_s);
        if (id > 0){
            query += " AND _id = ?";
            args = new String[]{String.valueOf(id)};
        }
        Cursor cursor = database.rawQuery(query,args);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getCityEvent(int id){
        String args[] = null;
        String query = "SELECT " +
                "T1.title , " +
                "T1.events_text, " +
                "T1.last_mod, " +
                "T1.time, " +
                "T1.city, " +
                "T1.address, " +
                "T2.name, " +
                "T2._id, " +
                "T1._id " +
                "FROM events as T1, city as T2 WHERE T1.city = T2._id";
        if (id > 0){
            query += " AND T1._id = ? ";
            args = new String[]{String.valueOf(id)};
        }
        Cursor cursor = database.rawQuery(query,args);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getEvents(int id){
        String args[] = null;
        String query = "SELECT " +
                       "T1.title , " +
                       "T1.events_text, " +
                       "T1.last_mod, " +
                       "T1.time, " +
                       "T1.city, " +
                       "T1.address, " +
                       "T2.name, " +
                       "T2._id, " +
                       "T1._id " +
                       "FROM events as T1, city as T2 WHERE T1.city = T2._id";
        if (id > 0){
            query += " AND T2.country_id = ?  ORDER BY time asc";
            args = new String[]{String.valueOf(id)};
        }
        Cursor cursor = database.rawQuery(query,args);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getNamas(int id){
        String args[] = null;
        String query = "select _id, name, time, flag, miss from namas";
        if (id != -1){
            query += " WHERE _id = ?";
            args = new String[]{String.valueOf(id)};
        }
        Cursor cursor = database.rawQuery(query,args);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getNewsStateByText(String title,String text){
        String query = "select _id from newsstate WHERE news_text = ? and title = ?";
        Cursor cursor = database.rawQuery(query,new String[]{text,title});
        if (cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;
    }

    public Cursor getRSS(String country){
        String query = "select _id, link, country from rss where country = ?";
        Cursor cursor = database.rawQuery(query,new String[]{country});
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;
    }

    public Cursor getRadio(String country){
        String query = "select _id, link, name , img, country from radio where country = ?";
        Cursor cursor = database.rawQuery(query,new String[]{country});
        if(cursor != null){
            cursor.moveToFirst();
        }
        return  cursor;
    }

    public Cursor getCountryList(int id){
        String[] args = null;
        String query = "select _id, country, name from country";
        if (id > 0){
            query += " WHERE _id = ?";
            args = new String[]{String.valueOf(id)};
        }
        Cursor cursor = database.rawQuery(query,args);
        if (cursor!= null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getRequestOrg(int id){
        String[] args = null;
        String query = "select _id, json, modification from req_org";
        if (id > 0){
            query += " WHERE _id = ?";
            args = new String[]{String.valueOf(id)};
        }
        Cursor cursor = database.rawQuery(query,args);
        if (cursor!= null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getCountryName(int id){
        String[] args = null;
        String query = "select _id,country,name from country ";
        if (id > 0){
            query += " WHERE _id = ?";
            args = new String[]{String.valueOf(id)};
        }

        Cursor cursor = database.rawQuery(query,args);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }


    public Cursor getListSourceByQuery(int city, int category, String queryString) {
        String[] args = new String[]{"%" + queryString + "%","%" + queryString + "%"};

        String query = "SELECT _id, name, address FROM organization ";

        if (city != -1 || category != -1){
            if (city != -1 && category != -1) query += "WHERE id_city="+String.valueOf(city)+" AND id_category="+String.valueOf(category);
            if (city != -1 && category == -1) query += "WHERE id_city="+String.valueOf(city);
            if (city == -1 && category != -1) query += "WHERE id_category="+String.valueOf(category);
        }

        query += " AND ( name LIKE ? OR address LIKE ?  ) ";


        Cursor cursor = database.rawQuery(query,args);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}

class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATA_BASE_NAME = "base_of_org";
    public static final String ON_DATA_BASE_CREATE_ORGANIZATION = "CREATE  TABLE organization  (  "+
            " _id integer primary key, name text not null,description text, "+
            " id_city integer, address text, t_number text, site text, id_category integer,"+
            " last_mod text , email text, lat text, lng text );";
    public static final String ON_DATA_BASE_CREATE_CITY = " CREATE TABLE city (_id primary key, name text not null, "+
            "last_mod text, x text, y text, tzone integer, country_id integer); ";

    public static final String ON_DATA_BASE_CREATE_CATEGORY = " CREATE TABLE category(_id primary key, name text " +
            "not null, last_mod text);";

    public static final String ON_DATA_BASE_CREATE_NEWS = " CREATE TABLE news (_id INTEGER PRIMARY KEY AUTOINCREMENT, title text" +
            ", news_text text, id_source text, last_mod text, url text);";

    public static final String ON_DATA_BASE_CREATE_EVENTS = " CREATE TABLE events (_id primary key, title text, " +
            "events_text text, last_mod text, time text, city text, address text);";

    public static final String ON_DATA_BASE_CREATE_NEWSSOURCE = " CREATE TABLE newssource(_id primary key, name text " +
            "not null, source_text text, last_mod text);";

    public static final String ON_DATA_BASE_CREATE_NAMAS = " CREATE TABLE namas (_id integer primary key autoincrement," +
            " name text, time text, flag integer,miss integer);";

    public static final String ON_DATA_BASE_CREATE_NEWS_STATE = " create table newsstate ( _id integer primary key autoincrement," +
            " title text, news_text text);";

    public static final String ON_DATA_BASE_CREATE_RSS = " create table rss (_id integer primary key, link text," +
            " country text);";

    public static final String ON_DATA_BASE_CREATE_RADIO = " create table radio (_id integer primary key, link text," +
            "name text, img text, country text);";

    public static final String ON_DATA_BASE_CREATE_COUNTRY = " create table country (_id integer primary key , country text ," +
            " name text);";
    public static final String ON_REQUEST_ORGANIZATION = " create table req_org (_id integer primary key autoincrement, json text ," +
            " modification integer);";


    public DatabaseHelper(Context context){
        super(context,DATA_BASE_NAME,null,DATA_BASE_VERSION);
        this.context = context;
    }
    Context context;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_ORGANIZATION);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_CITY);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_CATEGORY);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_NEWS);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_NEWSSOURCE);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_EVENTS);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_NAMAS);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_NEWS_STATE);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_RADIO);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_RSS);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_COUNTRY);
        sqLiteDatabase.execSQL(ON_REQUEST_ORGANIZATION);
        fillNamasTable(sqLiteDatabase);
    }

    void fillNamasTable(SQLiteDatabase sqLiteDatabase){
        int utc = 4;
        double lat = 55.751667;
        double lon = 37.617778;

        PrayTime prayers = new PrayTime();

        prayers.setTimeFormat(prayers.Time24);

        prayers.setCalcMethod(prayers.Karachi);

        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        String[] praysNames = new String[]{"Fajr","Sunrise","Dhuhr","Asr","Maghrib","Isha"};
        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal,
                lat, lon, utc);
        for (int i = 0,j = 0; i < 6; i ++){
            if (i == 4) j++;
            insertPray(praysNames[i],PrayTime.getNamasTimeInMillis(prayerTimes.get(j++)),sqLiteDatabase);
        }
    }

    void insertPray(String name,String time,SQLiteDatabase sqLiteDatabase){
        String query = " insert into namas (name , time , flag , miss ) values ( ?, ?, ?, ?) ";
        sqLiteDatabase.execSQL(query,new String[]{name,time,String.valueOf(0),String.valueOf(0)});
    }

    private static final int DATA_BASE_VERSION = 8;

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        if (i != i2){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS city");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS radio");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS rss");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS country");
            sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_RADIO);
            sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_RSS);
            sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_COUNTRY);
            sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_CITY);
            sqLiteDatabase.execSQL(ON_REQUEST_ORGANIZATION);

        }

    }
}
