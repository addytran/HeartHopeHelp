package com.example.taitran.hearthopehelp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    LoginButton fbLoginButton;
    CallbackManager callbackManager;
    public static String userName;
    public static String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.hhh);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        VideoView backgroundVideo = (VideoView) findViewById(R.id.loginVideoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.homeless_background);
        backgroundVideo.setVideoURI(uri);
        backgroundVideo.start();

        //fb login button starts here
        fbLoginButton = (LoginButton) findViewById(R.id.login_button);
        fbLoginButton.setReadPermissions("public_profile");
        callbackManager = CallbackManager.Factory.create();

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            @Override
            public void onSuccess(LoginResult loginResult) {

                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            Log.v("facebook - profile", profile2.getFirstName());
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("userName", profile2.getName());
                            editor.commit();
                            mProfileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userName", profile.getName());
                    editor.commit();
                }
                startActivity(i);
                finish();

            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Facebook login is canceled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                //Toast.makeText(MainActivity.this, "Oops, there is something wrong!!", Toast.LENGTH_LONG).show();
                startActivity(i);
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLoggedIn()){
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
            finish();
        }
    }

    public boolean isLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }
}
