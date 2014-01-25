package com.comfymobile.saadat.activity;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;
import com.google.analytics.tracking.android.EasyTracker;

import java.io.IOException;

/**
 * User: Nixy
 * Date: 02.05.13
 * Time: 22:47
 */
public class RadioActivity extends SherlockActivity {
        private ImageView playButton;

        private MediaPlayer mediaPlayer;
        private Player player;
        PhoneStateListener phoneStateListener;
    public static final String RADIO_URL = "http://s02.radio-tochka.com:8630/radio";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setContentView(R.layout.radio);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        mediaPlayer.pause();
                    } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                        //mediaPlayer.start();
                    } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        mediaPlayer.pause();
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
        protected void onResume(){
            super.onResume();
            playButton.setImageResource(R.drawable.btn_play);
            if (player == null){
                player = new Player();
                player.execute(RADIO_URL);
            }
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
        }

        private void initUI(){
            if (player == null){
                player = new Player();
                player.execute(RADIO_URL);
            }

            playButton = (ImageView) findViewById(R.id.playbutton);
            playButton.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                       if (!mediaPlayer.isPlaying()){
                             mediaPlayer.start();
                             playButton.setImageResource(R.drawable.btn_pause);
                       }else {
                             playButton.setImageResource(R.drawable.btn_play);
                             mediaPlayer.pause();
                        }
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

        class Player extends AsyncTask<String, Void, Boolean> {
            private ProgressDialog progress;

            @Override
            protected Boolean doInBackground(String... params) {
                // TODO Auto-generated method stub
                Boolean prepared;
                try {
                    mediaPlayer.setDataSource(params[0]);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // TODO Auto-generated method stub

                            playButton.setImageResource(R.drawable.btn_play);
                            mediaPlayer.stop();
                            mediaPlayer.reset();
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

            @Override
            protected void onPostExecute(Boolean result) {

                super.onPostExecute(result);
                if (progress.isShowing()) {
                    progress.cancel();
                }

            }

            public Player() {
                progress = new ProgressDialog(RadioActivity.this);
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                this.progress.setMessage("Подключение...");
                this.progress.show();

            }
        }

        @Override
        protected void onPause() {
            // TODO Auto-generated method stub
            super.onPause();
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }

}
