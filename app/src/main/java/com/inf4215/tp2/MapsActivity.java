package com.inf4215.tp2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, LocationSource {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private OnLocationChangedListener mListener;
    private Location mLastKnownLocation;
    private String adresseDepart;
    private String adresseArrivee;
    private PointDeMarquage depart;
    private PointDeMarquage arrivee;
    private ArrayList<PointDeMarquage> points = new ArrayList<PointDeMarquage>();
    private String provider;
    private boolean trackingEnabled = false;
    private boolean locationEnabled = false;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        connectionResult = connectionResult;
    }

    public class PointDeMarquage {
        public double latitude;
        public double longitude;
        public double altitude;
        // Direction array 3 float déplacement
        // Distance relative parcourue double
        // Distance Total double
        // Mode De Localisation (GPS)
        // Alarme + capture image d'identification de l'environnement
        // Niveau de batterie
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //On doit recevoir les points de depart et d'arrivee en parametre (string)
        //On utilisera geocoder pour obtenir la latitude et logitude de ces points de depart pour creer les points de marquages

        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            adresseDepart = extras.getString("depart");
            adresseArrivee = extras.getString("arrivee");
        }

        depart = getLatLongFromAddress(adresseDepart);
        arrivee = getLatLongFromAddress(adresseArrivee);

        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        setUpMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void RetrievePositionInformation() {
        PointDeMarquage point = new PointDeMarquage();
        //Location location = locationManager.getLastKnownLocation(provider);
        Location location = mLastKnownLocation;
        if (location != null) {
            point.latitude = location.getLatitude();
            point.longitude = location.getLongitude();
            point.altitude = location.getAltitude();
            String markerInfo = "Marker " + points.size() + "\nLatitude : " + point.latitude + "\nLongitude : " + point.longitude + "\nAltitude : " + point.altitude;
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(point.latitude, point.longitude))
                    .title(markerInfo)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
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
        locationRequest.setSmallestDisplacement(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
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
        //Location location = locationManager.getLastKnownLocation(provider);

        if (depart != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(depart.latitude, depart.longitude))
                    .title("Depart")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        if (arrivee != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(arrivee.latitude, arrivee.longitude))
                    .title("Arrivee")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

        trackingEnabled = true;
        final Handler handler = new Handler();
        handler.post(new Runnable() {

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
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void deactivate() {
        mListener = null;
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

        /*if( mListener != null ) {
            mListener.onLocationChanged(location);
        }*/

        if (location != null)
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

    public void onBackPressed() {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Menu")
                .setMessage("Voulez vous revenir au menu?")

                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private PointDeMarquage getLatLongFromAddress(String address) {
        double lat = 0.0, lng = 0.0;

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                PointDeMarquage point = new PointDeMarquage();
                point.latitude = addresses.get(0).getLatitude();
                point.longitude = addresses.get(0).getLongitude();
                return point;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    /*public PointDeMarquage getLatLongFromAddress(String youraddress) {
        youraddress.replace(" ", "%20");
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                youraddress + "&sensor=false";
        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            PointDeMarquage point = new PointDeMarquage();
            point.latitude = lat;
            point.longitude = lng;
            return point;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}