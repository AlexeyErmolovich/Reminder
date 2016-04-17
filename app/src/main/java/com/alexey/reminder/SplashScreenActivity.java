package com.alexey.reminder;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.container);
        ObjectAnimator animator = ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f).setDuration(2000);
        animator.start();
        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
