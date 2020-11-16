package com.piceadev.gpssurvey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.osmdroid.views.MapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private Button btnStartSurvey, btnCollectPoint, btnCollectLine, btnSettings;
    private LocationHelper locationHelper;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionsIfNecessary(new String[] {
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        //inflate and create the map
        setContentView(R.layout.activity_main);

        locationHelper = new LocationHelper(this);
        fragmentManager = this.getSupportFragmentManager();

        btnStartSurvey = findViewById(R.id.btnStartSurvey);
        btnCollectPoint = findViewById(R.id.btnCollectPoint);
        btnCollectLine = findViewById(R.id.btnCollectLine);
        btnSettings = findViewById(R.id.btnSettings);

        btnCollectPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationHelper.collectPointAverage(20);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public LocationHelper getLocationHelper () {
        return locationHelper;
    }

    public MapFragment getMapFragment () {
        return (MapFragment) fragmentManager.findFragmentById(R.id.mapFragment);
    }
}
