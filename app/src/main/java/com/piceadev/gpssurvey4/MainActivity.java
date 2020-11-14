package com.piceadev.gpssurvey4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnStartSurvey, btnCollectPoint, btnCollectLine, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartSurvey = findViewById(R.id.btnStartSurvey);
        btnCollectPoint = findViewById(R.id.btnCollectPoint);
        btnCollectLine = findViewById(R.id.btnCollectLine);
        btnSettings = findViewById(R.id.btnSettings);
    }
}