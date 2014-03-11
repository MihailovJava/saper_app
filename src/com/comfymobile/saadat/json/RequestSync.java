package com.comfymobile.saadat.json;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Author Grinch
 * Date: 11.03.14
 * Time: 19:19
 */
public class RequestSync extends AsyncTask<Void,Void,Void>{
    Context context;
    public RequestSync(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }
}
