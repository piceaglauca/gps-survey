package com.piceadev.gpssurvey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Locale;

public class LocationHelper {
    final int PERMISSION_ID = 1;
    final int MAX_HORIZ_M = 5;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private ArrayList<Location> locationList;

    private Activity context;

    protected boolean isLogging;

    public LocationHelper(Activity context) {
        this.context = context;

        isLogging = false;
        locationList = new ArrayList<>();

        if (!checkPermissions()) {
            requestPermissions();
        }

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    protected LocationManager getLocationManager () {
        return locationManager;
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                context,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @SuppressLint("MissingPermission")
    public void startLocationLogging(TextView tvSatFixes) {
        final TextView tvLog = tvSatFixes;
        //locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (hasRequiredAccuracy(location)) {
                    locationList.add(location);
                    updateSatFixes(tvLog, locationList.size());
                }
            }
        };

        if (checkPermissions()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            isLogging = true;
        }
    }

    @SuppressLint("MissingPermission")
    public void collectPointAverage (int fixes) {
        TextView tvCollectPoint = context.findViewById(R.id.tvCollectPoint);
        tvCollectPoint.setVisibility(View.VISIBLE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (locationList.size() >= fixes) {
                    stopLogging();
                    tvCollectPoint.setText(String.format(Locale.CANADA, "Point collected. Lat: %.6f, Long: %.6f, Accuracy: %.1fm", getAverageLatitude(), getAverageLongitude(), getAverageAccuracy()));
                } else {
                    if (hasRequiredAccuracy(location)) {
                        locationList.add(location);
                        tvCollectPoint.setText(String.format(Locale.CANADA, "Collecting Point: %d/%d fixes.", locationList.size(), fixes));
                    }
                }
            }
        };

        if (checkPermissions()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            isLogging = true;
        }
    }

    @Nullable
    @SuppressLint("MissingPermission")
    public GeoPoint getLastKnownLocation() {
        if (!checkPermissions()) {
            requestPermissions();
        }

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return new GeoPoint (lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    public void stopLogging() {
        locationManager.removeUpdates(locationListener);
        isLogging = false;
    }

    private void updateLocationLog (TextView tvLog, Location location) {
        String contents = tvLog.getText().toString();

        contents = String.format (Locale.CANADA, "%s\n%s", contents, location.toString());

        tvLog.setText(contents);
    }

    private void updateSatFixes (TextView tvLog, int satFixes) {
        tvLog.setText(String.format (Locale.CANADA, "%d", satFixes));
    }

    public int getNumberOfFixes () {
        return locationList.size();
    }

    public double getAverageLatitude () {
        double averageLatitude = 0;
        for (int i = 0; i < locationList.size(); i++) {
            averageLatitude += locationList.get(i).getLatitude();
        }
        return averageLatitude / locationList.size();
    }

    public double getAverageLongitude () {
        double averageLongitude = 0;
        for (int i = 0; i < locationList.size(); i++) {
            averageLongitude += locationList.get(i).getLongitude();
        }
        return averageLongitude / locationList.size();
    }

    public double getAverageAccuracy () {
        double accuracy = 0;
        for (int i = 0; i < locationList.size(); i++) {
            accuracy += locationList.get(i).getAccuracy();
        }
        return accuracy / locationList.size();
    }

    private boolean hasRequiredAccuracy (Location location) {
        return location.hasAccuracy() && location.getAccuracy() <= MAX_HORIZ_M;
    }
}
