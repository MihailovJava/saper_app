package com.comfymobile.saadat.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.comfymobile.saadat.R;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nixy on 10.03.14.
 */
public class RadioAdapter extends CursorAdapter {

    LayoutInflater inflater;
    List<String> url;
    SharedPreferences pref;

    public RadioAdapter(Context context, Cursor c) {
        super(context, c);
        url = new ArrayList<String>();
        inflater = LayoutInflater.from(context);
        pref = PreferenceManager.getDefaultSharedPreferences(context);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  inflater.inflate(R.layout.radio_list_item,parent,false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        TextView name = (TextView) view.findViewById(R.id.textView);

        String sName = cursor.getString(cursor.getColumnIndex("name"));

        int cur = pref.getInt("radio_link",0);

        if (cursor.getPosition() == cur)
            view.setBackgroundColor(context.getResources().getColor(R.color.radio_cheked));
        else
            view.setBackgroundColor(context.getResources().getColor(R.color.default_color));

        String path = context.getExternalCacheDir()+"/"+
                cursor.getString(cursor.getColumnIndex("_id"))+
                ".png";
        File imgFile = new  File(path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            img.setImageBitmap(myBitmap);
        }

        name.setText(sName);
    }

}
