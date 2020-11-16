package com.piceadev.gpssurvey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private MapView mMapView;
    private MyLocationNewOverlay mLocationOverlay;
    //private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;

    private MainActivity context;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LocationHelper locationHelper;

    private TextView tvSats;
    private TextView tvHDOP;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);

        this.context = (MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load/initialize the osmdroid configuration, this can be done
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        // setting this before the layout is inflated is a good idea
        // it should ensure that the map has a writable location for the map chace, even without permissions
        // if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance()
        // see also StorageUtils
        // note, the load method also sets the HTTP User Agent to your applications package name, abusing osm
        // tile servers will get you banned based on this string
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //mMapView = new MapView (inflater.getContext());

        //return mMapView;

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //final Context context = this.getActivity();

        mMapView = (MapView) context.findViewById (R.id.map);
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        // user location overlay
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);

        // mini map will be 1/5th of the screen
        //mMinimapOverlay = new MinimapOverlay(context, mMapView.getTileRequestCompleteHandler());
        //mMinimapOverlay.setWidth(dm.widthPixels / 5);
        //mMinimapOverlay.setHeight(dm.heightPixels / 5);
        //mMapView.getOverlays().add(this.mMinimapOverlay);

        // scale bar
        mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mMapView.getOverlays().add(this.mScaleBarOverlay);

        // needed for pinch zooms
        mMapView.setMultiTouchControls(true);

        // scales tiles to the current screens DPI. helps with readability of labels
        mMapView.setTilesScaledToDpi(true);

        mMapView.getController().setZoom(9.5);
        mMapView.getController().setCenter(new GeoPoint(50.58653, -127.08024)); // Port McNeill

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateGPSStatusFields (location);
            }
        };

        tvSats = context.findViewById(R.id.tvSats);
        tvHDOP = context.findViewById(R.id.tvHDOP);
    }

    private void updateGPSStatusFields (Location location) {
        Bundle extras = location.getExtras();

        if (extras.containsKey("satellites")) {
            tvSats.setText(String.format(Locale.CANADA, "%d", extras.getInt("satellites")));
        }

        if (extras.containsKey("hdop")) {
            tvSats.setText(String.format(Locale.CANADA, "%f", extras.getFloat("hdop")));
        }

        Toast.makeText(context, String.format (Locale.CANADA, "bundle length: %s", extras.toString()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause () {
        mMapView.onPause();
        super.onPause();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume () {
        super.onResume();
        mMapView.onResume();

        locationHelper = context.getLocationHelper ();
        locationManager = locationHelper.getLocationManager();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //this part terminates all of the overlays and background threads for osmdroid
        //only needed when you programmatically create the map
        mMapView.onDetach();
    }
}
