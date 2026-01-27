package com.example.selfmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView tvProgress;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.splash_progress_bar);
        tvProgress = findViewById(R.id.splash_progress_percentage);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            tvProgress.setText(progressStatus + "%");
                        }
                    });

                    try {

                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                finish();
            }
        }).start();
    }
}