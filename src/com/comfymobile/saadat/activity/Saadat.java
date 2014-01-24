package com.comfymobile.saadat.activity;

import android.app.Application;
import android.content.Context;
import com.parse.Parse;
import com.parse.PushService;

/**
 * Author Grinch
 * Date: 24.01.14
 * Time: 23:00
 */
public class Saadat extends Application {

    private Context context;

    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        Parse.initialize(this, "XlxEtkjhSKTrI0tAbMxtQATXY1UdDcoFN9F4YFxC", "k0pz8d73j1UM7LB3qz2iuULXGQwHmSSQysizAsDl");
        PushService.setDefaultPushCallback(this, MenuActivity.class);
    }
}