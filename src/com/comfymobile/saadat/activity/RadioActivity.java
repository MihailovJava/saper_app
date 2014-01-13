package com.comfymobile.saadat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.comfymobile.saadat.R;

import java.io.IOException;

/**
 * User: Nixy
 * Date: 02.05.13
 * Time: 22:47
 */
public class RadioActivity extends SherlockActivity {
        private Button playbutton;
        private Button backbutton;


        private MediaPlayer mediaPlayer;
        private Player player;

        public static final String RADIO_URL = "http://s02.radio-tochka.com:8630/radio";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setContentView(R.layout.radio);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            initUI();
        }

        @Override
        protected void onResume(){
            super.onResume();
            playbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play));
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

            playbutton = (Button) findViewById(R.id.playbutton);
            playbutton.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                       if (!mediaPlayer.isPlaying()){
                             mediaPlayer.start();
                             playbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pause));
                       }else {
                             playbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play));
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

                            playbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play));
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
