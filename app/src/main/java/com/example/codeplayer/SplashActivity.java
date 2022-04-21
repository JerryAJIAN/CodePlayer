package com.example.codeplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * @author ASUS_JAJIAN
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                //去标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.activity_splash);
/*
        Intent intent = new Intent(this,PlayService.class);
        startService(intent);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, com.example.codeplayer.MainActivity.class));
                finish();
            }
        },3000);*/
    }

}
