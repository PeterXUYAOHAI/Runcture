package com.example.yaoha_000.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Splash extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    private int[] data = new int[50];

    int hasData = 0;
    int progressStatus = 0;


    float currentDegree = 0f;
    float degree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {

                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);

                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, SPLASH_TIME_OUT);

        final ProgressBar bar = (ProgressBar) findViewById(R.id.bar);
        final ImageView img = (ImageView)findViewById(R.id.imgLogo);


        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x51) {
                    RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    degree = progressStatus * 5f;
                    ra.setDuration(200);
                    ra.setFillAfter(true);
                    img.startAnimation(ra);
                    currentDegree = -degree;
                    bar.setProgress(progressStatus);

                }
            }
        };
        new Thread() {
            public void run() {
                while (progressStatus < 50) {
                    progressStatus = doWork();
                    Message m = new Message();
                    m.what = 0x51;
                    handler.sendMessage(m);
                }
            }
        }.start();
    }

    private int doWork() {
        data[hasData++] = (int) (Math.random() * 50);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasData;
    }


}
