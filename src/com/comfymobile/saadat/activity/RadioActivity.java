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
import com.comfymobile.saadat.R;

import java.io.IOException;

/**
 * User: Nixy
 * Date: 02.05.13
 * Time: 22:47
 */
public class RadioActivity extends Activity {
        private Button playbutton;
        private Button backbutton;
        /**
         * help to toggle between play and pause.
         */
        private boolean playPause;
        private MediaPlayer mediaPlayer;
        /**
         * remain false till media is not completed, inside OnCompletionListener make it true.
         */
        private boolean intialStage = true;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setContentView(R.layout.radio);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            initUI();
        }

        @Override
        protected void onResume(){
            super.onResume();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            intialStage = true;
            playPause = false;
            playbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_button));
        }

        private void initUI(){

            playbutton = (Button) findViewById(R.id.playbutton);
            playbutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!playPause) {
                        playbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_button));
                        if (intialStage)
                            new Player()
                                    .execute("http://s02.radio-tochka.com:8630/radio");
                        else {
                            if (!mediaPlayer.isPlaying())
                                mediaPlayer.start();
                        }
                        playPause = true;
                    } else {
                        playbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_button));
                        if (mediaPlayer.isPlaying())
                            mediaPlayer.pause();
                        playPause = false;
                    }
                }
            });

            backbutton = (Button) findViewById(R.id.back_button);
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
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
                            intialStage = true;
                            playPause=false;
                            playbutton.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_button));
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

                mediaPlayer.start();

                intialStage = false;
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
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
}
