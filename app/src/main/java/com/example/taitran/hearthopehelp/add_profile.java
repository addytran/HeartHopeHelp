package com.example.taitran.hearthopehelp;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.internal.Utility;
import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class add_profile extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    //ImageView
    private ImageView addProfileImage;

    //edittext starts here
    private EditText aName;
    private EditText aAge;
    private EditText aHometown;
    private EditText aUsuallyFound;
    private EditText aStory;

    //for current location
    private Button submitButton;
    private Button cancelButton;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double longitude, latitude;
    //for database
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.hhh);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_add_profile);

        addProfileImage = (ImageView) findViewById(R.id.aPicButton);
        addProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        aName = (EditText) findViewById(R.id.aNameEditText);
        aAge = (EditText) findViewById(R.id.aAgeEditText);
        aHometown = (EditText) findViewById(R.id.aHometownEditText);
        aUsuallyFound = (EditText) findViewById(R.id.aWhereToFindEditText);
        aStory = (EditText) findViewById(R.id.aStoryEditText);

        submitButton = (Button) findViewById(R.id.aSubmitButton);

        //database implementation starts here
        mDatabase = FirebaseDatabase.getInstance().getReference();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Toast.makeText(add_profile.this, longitude + " " + latitude, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_Button();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(add_profile.this, "Current Location Updated", Toast.LENGTH_SHORT).show();
                submitPost();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void submitPost() {
        final String name = aName.getText().toString();
        final String age = aAge.getText().toString();
        final String hometown = aHometown.getText().toString();
        final String usuallyFound = aUsuallyFound.getText().toString();
        final String story = aStory.getText().toString();
        final double lng = longitude;
        final double lat = latitude;

        Toast.makeText(add_profile.this, "Adding profile to database...", Toast.LENGTH_SHORT).show();

        mDatabase.child("profiles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = mDatabase.child("profiles").push().getKey();
                homelessProfile newProfile = new homelessProfile(name, age, hometown, usuallyFound
                ,story, lng, lat);

                Map<String, Object> profileValue = newProfile.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/profile/" + key, profileValue);
                mDatabase.updateChildren(childUpdates);

                Toast.makeText(add_profile.this, "Add Profile Success", Toast.LENGTH_LONG).show();

                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(add_profile.this, "Error Adding New Profile\nPlease report this bug or try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_Button();
                break;
            default:
                break;
        }
    }

    private void configure_Button() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
            return;
        }

        Toast.makeText(add_profile.this, "Current Location Updated", Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logoutMenu:
                //logout is clicked
                LoginManager.getInstance().logOut();
                Toast.makeText(add_profile.this, "Logout Successful", Toast.LENGTH_LONG).show();
                startActivity(new Intent(add_profile.this, MainActivity.class));
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



    private void selectImage(){
        final CharSequence[] items = {"Take Photo", "Choose From Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(add_profile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(items[i].equals("Take Photo")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
                else if(items[i].equals("Choose From Library")){
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                }
                else if(items[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if(requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if(data != null){
            try{
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),
                        data.getData());
            }
            catch(IOException e){
                e.printStackTrace();
            }
            addProfileImage.setImageBitmap(bm);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        addProfileImage.setImageBitmap(thumbnail);
    }
}
