package com.inf4215.tp2;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Location mLastKnownLocation;
    private PointDeMarquage depart;
    private PointDeMarquage arriver;
    private ArrayList<PointDeMarquage> points = new ArrayList<PointDeMarquage>();
    private String provider;
    private boolean trackingEnabled = false;
    private boolean locationEnabled = false;

    public class PointDeMarquage {
        public double latitude;
        public double longitude;
        public double altitude;
        // Direction déplacement
        // Distance relative parcourue
        // Distance Total
        // Mode De Localisation (GPS)
        // Alarme + capture image d'identification de l'environnement
        // Niveau de batterie
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        setUpMap();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                //.addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void RetrievePositionInformation() {
        PointDeMarquage point = new PointDeMarquage();
        //Location location = locationManager.getLastKnownLocation(provider);
        Location location = mLastKnownLocation;
        if(location != null){
            point.latitude = location.getLatitude();
            point.longitude = location.getLongitude();
            point.altitude = location.getAltitude();
            String markerInfo = "Marker " + points.size() + "\nLatitude : " + point.latitude + "\nLongitude : " + point.longitude + "\nAltitude : " + point.altitude;
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(point.latitude, point.longitude))
                    .title(markerInfo));
            points.add(point);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected())
            stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void setUpMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMyLocationEnabled(true);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if(location != null){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("Marker " + points.size()));
        }

        trackingEnabled = true;
        final Handler handler=new Handler();
        handler.post(new Runnable(){

            @Override
            public void run() {
                if (trackingEnabled) {
                    RetrievePositionInformation();
                    handler.postDelayed(this, 5000); // Change this time in function of sampling frequency
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //locationEnabled = true;

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        locationEnabled = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
    }

    /*@Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }*/
}
