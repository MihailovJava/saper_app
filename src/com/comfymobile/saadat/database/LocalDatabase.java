package com.comfymobile.saadat.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public static LocalDatabase getInstance(Context context) {
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

    public void updateOrganization(int id, String name, String description, int id_city,
                                        String address, String t_number, String site,
                                        int id_category,String last_mod,String email){
        String query = "insert or replace into organization " +
                "(_id, name, description, id_city, address, t_number, site, id_category, last_mod, email)" +
                " values (?,?,?,?,?,?,?,?,?,?)";

        database.execSQL(query,new String[]{String.valueOf(id),name,description,String.valueOf(id_city),
                address,t_number,site,String.valueOf(id_category),last_mod,email});
    }

    public void updateCity(int id_city, String name, String last_mod, String x, String y, int tzone){
        String query = "insert or replace into city (_id, name, last_mod, x, y, tzone) values (?,?,?,?,?,?)";
        database.execSQL(query,new String[]{String.valueOf(id_city),name,last_mod,x,y,String.valueOf(tzone)});
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

    public Cursor getCitySource(){
        String query = "SELECT _id , name, x, y, tzone FROM city";
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
        String query = "SELECT _id , name FROM category WHERE _id IN (SELECT id_category " +
                       "FROM organization WHERE id_city= ? );" ;

        Cursor cursor = database.rawQuery(query,new String[]{String.valueOf(cityID)});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getCitySource(int id){
          String query = "SELECT _id , name FROM city WHERE _id = ?";
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
            query += " AND T1._id = ?";
            args = new String[]{String.valueOf(id)};
        }
        Cursor cursor = database.rawQuery(query,args);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Добавить источник
     * @param id номер источника
     * @param title название
     * @param description описание
     * @param link ссылка
     */
    public void addSource(int id, String title, String description, String link){
        String query = "insert into sources (_id,title,description,link) values (?,?,?,?)";
        database.execSQL(query,new String[]{String.valueOf(id),title,description,link});
    }





}

class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATA_BASE_NAME = "base_of_org";
    public static final String ON_DATA_BASE_CREATE_ORGANIZATION = "CREATE TABLE organization (  "+
            " _id integer primary key, name text not null,description text, "+
            " id_city integer, address text, t_number text, site text, id_category integer,"+
            " last_mod text , email text );";
    public static final String ON_DATA_BASE_CREATE_CITY = " CREATE TABLE city (_id primary key, name text not null, "+
            "last_mod text, x text, y text, tzone integer); ";

    public static final String ON_DATA_BASE_CREATE_CATEGORY = " CREATE TABLE category(_id primary key, name text " +
            "not null, last_mod text);";

    public static final String ON_DATA_BASE_CREATE_NEWS = " CREATE TABLE news (_id INTEGER PRIMARY KEY AUTOINCREMENT, title text" +
            ", news_text text, id_source text, last_mod text, url text);";

    public static final String ON_DATA_BASE_CREATE_EVENTS = " CREATE TABLE events (_id primary key, title text, " +
            "events_text text, last_mod text, time text, city text, address text);";

    public static final String ON_DATA_BASE_CREATE_NEWSSOURCE = " CREATE TABLE newssource(_id primary key, name text " +
            "not null, source_text text, last_mod text);";


    public DatabaseHelper(Context context){
        super(context,DATA_BASE_NAME,null,4);
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        if (i != i2){
            sqLiteDatabase.execSQL("DROP TABLE newssource");
            sqLiteDatabase.execSQL("DROP TABLE organization");
            sqLiteDatabase.execSQL("DROP TABLE city");
            sqLiteDatabase.execSQL("DROP TABLE category");
            sqLiteDatabase.execSQL("DROP TABLE news");
            sqLiteDatabase.execSQL("DROP TABLE events");
            //context.openOrCreateDatabase(DATA_BASE_NAME,Context.MODE_PRIVATE,null);
        }
        onCreate(sqLiteDatabase);
    }
}
