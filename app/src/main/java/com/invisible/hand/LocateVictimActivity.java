package com.invisible.hand;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class LocateVictimActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap victimMap;
    private GoogleMap userMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public static Double latitude;
    public static Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_victim);
        Log.d("victim", "onCreate: "+FirstService.victimLatitude+" "+FirstService.victimLongitude);
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", FirstService.victimLatitude, FirstService.victimLongitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    */}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        victimMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(FirstService.victimLatitude, FirstService.victimLongitude);
        //LatLng sydney = new LatLng();
        victimMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        victimMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));



    }

    public void getUserLocation(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                locationManager.removeUpdates(locationListener);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

}
