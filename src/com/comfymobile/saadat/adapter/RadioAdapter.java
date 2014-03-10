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
        int cur = pref.getInt("radio_link",0);

        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        TextView name = (TextView) view.findViewById(R.id.textView);

        String sName = cursor.getString(cursor.getColumnIndex("name"));
        String sImg = cursor.getString(cursor.getColumnIndex("img"));

        if (url.indexOf(sName) == -1 ){
            url.add(sName);
            new DownloadImageTask(img).execute(sImg);
        }


        if (cursor.getPosition() == cur)
            view.setBackgroundColor(context.getResources().getColor(R.color.radio_cheked));
        else
            view.setBackgroundColor(context.getResources().getColor(R.color.default_color));
        name.setText(sName);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
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
            if (result != null)
                bmImage.setImageBitmap(result);
            else
                bmImage.setImageResource(R.drawable.btn_alarm_enable);
        }
    }
}
