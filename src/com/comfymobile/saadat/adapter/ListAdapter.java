package com.comfymobile.saadat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;

/**
 * User: Nixy
 * Date: 09.04.13
 * Time: 20:25
 */
public class ListAdapter extends ArrayAdapter<Void> {
    private final Context context;

    private Cursor source;

    public ListAdapter(Context context){
        super(context,0);
        this.context = context;
        source = new LocalDatabase(context).getListSource();
        source.moveToFirst();
    }

    @Override
    public int getCount(){
        return source.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (source.isAfterLast()){
            source.moveToFirst();
        }
        LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item,null);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView address = (TextView) rowView.findViewById(R.id.address);
        name.setText(source.getString(0));
        address.setText(source.getString(1));

        source.moveToNext();

        return rowView;
    }

}

