package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.client.JSONClient;


public class LoadingActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    public static Activity activity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity = this;
        setContentView(R.layout.loading);
        new JSONClient().synchronizeDataBase(this);
    }

    @Override
    public void onBackPressed() {
    }
}
