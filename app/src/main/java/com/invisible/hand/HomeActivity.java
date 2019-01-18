package com.invisible.hand;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private Button callHelpButton;
    private Button locateVictimButton;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public static Double latitude;
    public static Double longitude;
    public static  String userName;
    public static  String mobileNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth=FirebaseAuth.getInstance();
        userName=getIntent().getStringExtra("name");
        mobileNumber=getIntent().getStringExtra("mobile");
        progressBar=findViewById(R.id.progressBar);
        callHelpButton=findViewById(R.id.callHelpButton);
        callHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserLocation();
            }
        });
        locateVictimButton=findViewById(R.id.locateVictimButton);
        locateVictimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locateVictim();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.signOutMenuButton: {
                signOut();
                break;
            }
        }
        return true;
    }
    public void signOut() {
        Log.d("home", "signOut: outsss");
        mAuth.signOut();
        if (LoginActivity.mGoogleSignInClient == null) {
            Log.d("Home Activity", "signOut: in here");
            goToLoginActivity();
        } else {
            LoginActivity.mGoogleSignInClient.signOut();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                goToLoginActivity();
            }
            Log.d("user", "signOut: complete " + currentUser);
        }

    }

    private void goToLoginActivity(){
        Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    public void callForHelp(View view){
        Toast.makeText(this, R.string.looking_for_help, Toast.LENGTH_SHORT).show();
    }

    public void locateVictim(){
        Intent intent = new Intent(this, LocateVictimActivity.class);
        startActivity(intent);
        Log.d("ERRROR", "this studio is shit!");
    }



    public void getUserLocation() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("Sd", "GetUserLocation: in here");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("snn", "onLocationChanged: " + location.getLatitude() + " " + location.getLongitude());
                latitude=location.getLatitude();
                longitude=location.getLongitude();
                locationManager.removeUpdates(locationListener);
                Log.d("HomeActivity", "onLocationChanged: "+longitude+" "+latitude);
                progressBar.setVisibility(View.INVISIBLE);

                DialogBox dialogBox=new DialogBox(HomeActivity.this);
                Log.d("HomeActivity", "onClick: Calling dailog box");

                dialogBox.generateDialogBox();

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
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 1);

            return;

        } else {
            locationManager.requestLocationUpdates("gps", 0, 0, locationListener);

        }
    }


}
