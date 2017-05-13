package com.example.taitran.hearthopehelp;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    public String name;
    private DatabaseReference mDatabase;

    //current location check
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("HeartHopeHelp");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.hhh);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = sharedPreferences.getString("userName", "user");

        Toast.makeText(MapsActivity.this, "Login Success " + name, Toast.LENGTH_LONG).show();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Button toAddProfileButton = (Button)  findViewById(R.id.mAddProfileButton);
        toAddProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, add_profile.class));
            }
        });

        Button toBugReportButton = (Button) findViewById(R.id.mReportButton);
        toBugReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, BugReport.class));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logoutMenu:
                //logout is clicked
                LoginManager.getInstance().logOut();
                Toast.makeText(MapsActivity.this, "Logout Successful", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
                finish();
                return true;
            case R.id.settingsMenu:
                return true;
            case R.id.AboutUsMenu:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot homeless: dataSnapshot.getChildren()){
                    String key = homeless.getKey();
                    Map<String, Object> map = (Map<String, Object>) homeless.getValue();
                    String lng = (String) map.get("Longitude").toString();
                    String lat = (String) map.get("Latidude").toString();

                    String area = (String) map.get("usuallyFound");
                    String hometown = (String) map.get("hometown");

                    double longitude = Double.parseDouble(lng);
                    double latitude = Double.parseDouble(lat);
                    LatLng pos = new LatLng(longitude, latitude);
                    System.out.println("ID: " + key + "\n");
                    System.out.println("Longitude: " + longitude + " Latitude: " + latitude);
                    String name = (String) map.get("name");
                   //Toast.makeText(MapsActivity.this, "Name: " + name
                     //       + "\nLngLat: " + longitude + " " + latitude, Toast.LENGTH_LONG).show();
                    Marker m = mMap.addMarker(new MarkerOptions().position(pos).title(name).snippet(key));
                    String[] info = {area, hometown};
                    m.setTag(info);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                String key = marker.getSnippet();

                TextView commonFoundDisplay = (TextView) v.findViewById(R.id.infoCommonlyFoundDisplay);
                TextView hometownDisplay = (TextView) v.findViewById(R.id.infoHometownDisplay);
                TextView nameDisplay = (TextView) v.findViewById(R.id.infoNameDisplay);

                String[] t = (String[]) marker.getTag();
                commonFoundDisplay.setText("From " + t[0]);
                hometownDisplay.setText("Area: " + t[1]);
                nameDisplay.setText("Name: " + marker.getTitle());

                return v;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent goDetail = new Intent(MapsActivity.this, DetailProfileActivity.class);
                String key = marker.getSnippet();
                goDetail.putExtra("KEY", key);
                startActivity(goDetail);
            }
        });
        mMap.getUiSettings().setMapToolbarEnabled(false); //disable navigation and zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Seattle and move the camera
        LatLng zoompos = new LatLng(47.612421, -122.33738);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoompos ,10f));

    }






}
