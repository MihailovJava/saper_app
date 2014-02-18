package com.comfymobile.saadat.activity;

import android.app.ProgressDialog;
import android.content.Context;
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

        Player player;
        PhoneStateListener phoneStateListener;
        public static final String RADIO_URL = "http://s02.radio-tochka.com:8630/radio";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setContentView(R.layout.radio);

            player = Player.getInstance(this);
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
         player.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

            player.setPlayButton(playButton);
            if (!player.getPrepared() && player.getStatus() != AsyncTask.Status.RUNNING){
                player.execute(RADIO_URL);
            }
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
            private static boolean prepared = false;
            private MediaPlayer mediaPlayer;
            ImageView playButton;


            public MediaPlayer getMediaPlayer() {
                return mediaPlayer;
            }

            public void setPlayButton(ImageView playButton) {
                this.playButton = playButton;
            }

            public static boolean getPrepared(){
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
                }
                if ( me != null){
                    if ((me.getStatus() == Status.FINISHED || me.isCancelled() == true) && !me.prepared){
                        me = new Player(context);
                    }
                }
                return me;
            }


            @Override
            protected Boolean doInBackground(String... params) {
                // TODO Auto-generated method stub

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

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                this.progress.setMessage("Подключение...");
                this.progress.show();

            }
        }



}
