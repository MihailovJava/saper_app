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
                "(_id, name, description, id_city, address, t_number, site, id_category, last_mod)" +
                " values ("+String.valueOf(id)+", \""+name+"\", \""+description+"\", "+String.valueOf(id_city)+
                ", \""+address+"\", \""+t_number+"\", \""+site+"\", "+String.valueOf(id_category)+", \""+last_mod+"\")";
        database.execSQL(query);
    }

    public void updateCity(int id_city, String name, String last_mod){
        String query = "insert or replace into city " +
                "(_id, name, last_mod)" +
                " values ("+String.valueOf(id_city)+", \""+name+"\", \""+last_mod+"\")";
        database.execSQL(query);
    }
    public void updateCategory(int id_category, String name, String last_mod){
        String query = "insert or replace into category " +
                "(_id, name, last_mod)" +
                " values ("+String.valueOf(id_category)+", \""+name+"\", \""+last_mod+"\")";
        database.execSQL(query);
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
        String query = "SELECT _id , name FROM city";
        Cursor cursor = database.rawQuery(query,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getCategorySource(){
        String query = "SELECT _id , name FROM category";
        Cursor cursor = database.rawQuery(query,null);
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

}

class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATA_BASE_NAME = "base_of_org";
    public static final String ON_DATA_BASE_CREATE_ORGANIZATION = "CREATE TABLE organization(  "+
            " _id integer primary key, name text not null,description text, "+
            " id_city integer, address text, t_number text, site text, id_category integer,"+
            " last_mod text  );";
    public static final String ON_DATA_BASE_CREATE_CITY = " CREATE TABLE city(_id primary key, name text not null,"+
            "last_mod text ); ";

    public static final String ON_DATA_BASE_CREATE_CATEGORY = " CREATE TABLE category(_id primary key, name text " +
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
