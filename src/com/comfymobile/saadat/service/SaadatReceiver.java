package com.comfymobile.saadat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.comfymobile.saadat.activity.Saadat;

/**
 * Created by Nixy on 26.01.14.
 */
public class SaadatReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,SaadatService.class));
    }

}
