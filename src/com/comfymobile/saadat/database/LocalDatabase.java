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
    public LocalDatabase(Context context){
        helper = new DatabaseHelper(context);
        database = helper.getWritableDatabase();
    }

    public void clearDatabase(){
        database.execSQL("DROP TABLE organization");
        database.execSQL("DROP TABLE city");
        database.execSQL("DROP TABLE category");
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_ORGANIZATION);
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_CITY);
        database.execSQL(DatabaseHelper.ON_DATA_BASE_CREATE_CATEGORY);
    }
    public void updateOrganization(int id, String name, String description, int id_city,
                                        String address, String t_number, String site,
                                        int id_category,String last_mod){
        String query = "insert or replace into organization " +
                "(id, name, description, id_city, address, t_number, site, id_category, last_mod)" +
                " values ("+String.valueOf(id)+", \""+name+"\", \""+description+"\", "+String.valueOf(id_city)+
                ", \""+address+"\", \""+t_number+"\", \""+site+"\", "+String.valueOf(id_category)+", \""+last_mod+"\")";
        database.execSQL(query);
    }

    public void updateCity(int id_city, String name, String last_mod){
        String query = "insert or replace into city " +
                "(id_city, name, last_mod)" +
                " values ("+String.valueOf(id_city)+", \""+name+"\", \""+last_mod+"\")";
        database.execSQL(query);
    }
    public void updateCategory(int id_category, String name, String last_mod){
        String query = "insert or replace into category " +
                "(id_category, name, last_mod)" +
                " values ("+String.valueOf(id_category)+", \""+name+"\", \""+last_mod+"\")";
        database.execSQL(query);
    }

    public Cursor getListSource(){
        String query = "SELECT name , address FROM organization";
        return database.rawQuery(query,null);
    }

}

class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATA_BASE_NAME = "base_of_org";
    public static final String ON_DATA_BASE_CREATE_ORGANIZATION = "CREATE TABLE organization(  "+
            " id integer primary key, name text not null,description text, "+
            " id_city integer, address text, t_number text, site text, id_category integer,"+
            " last_mod text  );";
    public static final String ON_DATA_BASE_CREATE_CITY = " CREATE TABLE city(id_city primary key, name text not null,"+
            "last_mod text ); ";

    public static final String ON_DATA_BASE_CREATE_CATEGORY = " CREATE TABLE category( id_category primary key, name text " +
            "not null, last_mod text);";

    public DatabaseHelper(Context context){
        super(context,DATA_BASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_ORGANIZATION);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_CITY);
        sqLiteDatabase.execSQL(ON_DATA_BASE_CREATE_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        onCreate(sqLiteDatabase);
    }
}
