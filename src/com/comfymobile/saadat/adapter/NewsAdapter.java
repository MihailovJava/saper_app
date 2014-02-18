package com.comfymobile.saadat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;


/**
 * Created by Nixy on 08.02.14.
 */
public class NewsAdapter extends CursorAdapter {

    LayoutInflater inflater;
    Context context;

    public NewsAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.news_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView date = (TextView) view.findViewById(R.id.date);

        String sTitle = cursor.getString(cursor.getColumnIndex("title"));
        String sDescription = cursor.getString(cursor.getColumnIndex("news_text"));
        String sDate = cursor.getString(cursor.getColumnIndex("last_mod"));

        title.setText(sTitle);
        description.setText(sDescription);
        date.setText(sDate);

        Cursor state = LocalDatabase.getInstance(context).getNewsStateByText(title.getText().toString(),
                description.getText().toString());
        if (state != null){
            if (state.getCount() > 0)
                 view.setBackgroundColor(context.getResources().getColor(R.color.state_chkd_color));
            else
                view.setBackgroundColor(context.getResources().getColor(R.color.abs__background_holo_light));
        }
    }
}
