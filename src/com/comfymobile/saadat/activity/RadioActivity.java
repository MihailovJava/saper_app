package com.comfymobile.saadat.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.comfymobile.saadat.database.LocalDatabase;
import com.google.analytics.tracking.android.EasyTracker;

import java.io.IOException;
import java.util.Locale;

/**
 * User: Nixy
 * Date: 02.05.13
 * Time: 22:47
 */
public class RadioActivity extends SherlockActivity {
        private ImageView playButton;
        private ImageView prevButton;
        private ImageView nextButton;
        Context context;
        Player player;
        PhoneStateListener phoneStateListener;
        SharedPreferences preference;
        SharedPreferences.Editor editor;
        public static final String RADIO_URL = "http://s02.radio-tochka.com:8630/radio";

        String[] radioLink;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            context = this;

            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(R.string.ab_radio_title);

            setContentView(R.layout.radio);
            preference = PreferenceManager.getDefaultSharedPreferences(context);
            editor = preference.edit();

            String country_id = PreferenceManager.getDefaultSharedPreferences(context).getString("country_id", "1");
            Cursor country = LocalDatabase.getInstance(context).getCountryName(Integer.valueOf(country_id));
            Cursor radio = LocalDatabase.getInstance(context).getRadio(country.getString(country.getColumnIndex("country")));
            radioLink = new String[radio.getCount()];
            for (int i = 0; i < radio.getCount(); i++){
                radioLink[i] = radio.getString(radio.getColumnIndex("link"));
                radio.moveToNext();
            }

            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        player.getMediaPlayer().pause();
                    } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                        //mediaPlayer.start();
                    } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        player.getMediaPlayer().pause();
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if(mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
            initUI();
        }



    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player.getStatus() == AsyncTask.Status.RUNNING)
             player.cancel(true);
        if (!player.getMediaPlayer().isPlaying()){
            player.getMediaPlayer().release();
            player.prepared = false;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        player = Player.getInstance(context);
        player.setPlayButton(playButton);
        if (!isOnline()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Требуется интернет соединение")
                    .setNegativeButton("Выйти", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            if (!player.getPrepared() && player.getStatus() != AsyncTask.Status.RUNNING ){
                player.execute(radioLink);
            }
        }

        if (player.getMediaPlayer().isPlaying()){
            playButton.setImageResource(R.drawable.btn_pause);
        }else {
            playButton.setImageResource(R.drawable.btn_play);
        }
    }

    private void initUI(){
        playButton = (ImageView) findViewById(R.id.playbutton);
        playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if (!player.getMediaPlayer().isPlaying()){
                       player.getMediaPlayer().start();
                       playButton.setImageResource(R.drawable.btn_pause);
                   }else {
                       playButton.setImageResource(R.drawable.btn_play);
                       player.getMediaPlayer().pause();
                    }
            }
            });

        prevButton = (ImageView) findViewById(R.id.prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cur = preference.getInt("radio_link",0);
                cur = cur - 1 < 0 ? radioLink.length-1 : cur -1;
                editor.putInt("radio_link",cur);
                editor.commit();
                Player.getInstance(context).getMediaPlayer().reset();
                Player.getInstance(context).finished = true;
                player = Player.getInstance(context);
                player.execute(radioLink);
            }
        });

        nextButton = (ImageView) findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cur = preference.getInt("radio_link",0);
                cur = cur + 1 > radioLink.length-1 ? 0 : cur + 1;
                editor.putInt("radio_link",cur);
                editor.commit();
                Player.getInstance(context).getMediaPlayer().reset();
                Player.getInstance(context).finished = true;
                player = Player.getInstance(context);
                player.execute(radioLink);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //needs import android.view.MenuItem;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



        /**
         * preparing mediaplayer will take sometime to buffer the content so prepare it inside the background thread and starting it on UI thread.
         * @author piyush
         *
         */

        static class Player extends AsyncTask<String, Void, Boolean> {
            private ProgressDialog progress;

            private static Player me;
            private boolean prepared = false;
            private boolean finished = false;
            private MediaPlayer mediaPlayer;
            ImageView playButton;
            static SharedPreferences pref;

            public MediaPlayer getMediaPlayer() {
                return mediaPlayer;
            }

            public void setPlayButton(ImageView playButton) {
                this.playButton = playButton;
            }

            public boolean getPrepared(){
                return prepared;
            }

            private Player(Context context) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                progress = new ProgressDialog(context);
            }



            public static Player getInstance(Context context){
                if (me == null){
                    me = new Player(context);
                    pref =  PreferenceManager.getDefaultSharedPreferences(context);
                }
                if ( me != null){
                    if ((me.getStatus() == Status.FINISHED || me.isCancelled() == true) && !me.prepared){
                        me = new Player(context);
                        return me;
                    }
                    if (me.finished){
                        Player tmp = new Player(context);
                        tmp.setPlayButton(me.playButton);
                        me = tmp;
                        return me;
                    }
                }

                return me;
            }


            @Override
            protected Boolean doInBackground(String... params) {
                // TODO Auto-generated method stub

                try {
                    mediaPlayer.setDataSource(params[getCurrentLink(params.length)]);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // TODO Auto-generated method stub

                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            finished = true;
                            playButton.setImageResource(R.drawable.btn_play);
                        }
                    });
                    mediaPlayer.prepare();
                    prepared = true;
                } catch (IllegalArgumentException e) {
                    prepared = false;
                } catch (SecurityException e) {
                    prepared = false;
                } catch (IllegalStateException e) {
                    prepared = false;
                } catch (IOException e) {
                    prepared = false;
                }
                return prepared;
            }

            private int getCurrentLink(int length) {
                int cur = pref.getInt("radio_link",0);
                cur = cur  > length-1 ? 0 : cur ;
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("radio_link",cur);
                editor.commit();
                return cur;
            }


            @Override
            protected void onPostExecute(Boolean result) {
                playButton.setImageResource(R.drawable.btn_play);
                super.onPostExecute(result);
                if (progress.isShowing()) {
                    progress.cancel();
                }
                cancel(true);
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                this.progress.setMessage("Подключение...");
                this.progress.show();

            }
        }



}
