package com.example.taitran.hearthopehelp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

public class DetailProfileActivity extends AppCompatActivity {
    //info
    private TextView dID;
    private TextView dName;
    private TextView dAge;
    private TextView dHometown;
    private TextView dUsuallyFound;
    private TextView dStory;

    //two buttons
    private Button dNavigateButton;
    private Button dReportButton;

    //for database
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.hhh);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_detail_profile);

        Bundle extras = getIntent().getExtras();
        String profileKey = extras.getString("KEY");

        //pass key to display info
        profileInfoDisplay(profileKey);

        dNavigateButton = (Button) findViewById(R.id.dNavigationButton);
        dReportButton = (Button) findViewById(R.id.dProfileReportButton);

        dNavigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetailProfileActivity.this, "Sorry, This function is currently not available",
                        Toast.LENGTH_LONG).show();
            }
        });

        dReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetailProfileActivity.this, "Sorry, This function is currently not available",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void profileInfoDisplay(String key){
        final String profileKey = key;

        //initial these variables
        dID = (TextView) findViewById(R.id.dIDShown);
        dName = (TextView) findViewById(R.id.dNameShown);
        dAge = (TextView) findViewById(R.id.dAgeShown);
        dHometown = (TextView) findViewById(R.id.dHomeTownShown);
        dUsuallyFound = (TextView) findViewById(R.id.dUsuallyFoundShown);
        dStory = (TextView) findViewById(R.id.dStoryTextView);

        mDatabase = FirebaseDatabase.getInstance().getReference("profile");
        mDatabase.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String age = dataSnapshot.child("age").getValue().toString();
                String hometown = dataSnapshot.child("hometown").getValue().toString();
                String area = dataSnapshot.child("usuallyFound").getValue().toString();
                String story = dataSnapshot.child("story").getValue().toString();

                dName.setText("Name: " +name);
                dAge.setText("Age: " + age);
                dHometown.setText("From: " + hometown);
                dUsuallyFound.setText("Area: " + area);
                dStory.setText(story);

                Toast.makeText(DetailProfileActivity.this, "Profile of " + name,
                         Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dID.setText(key);

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
                Toast.makeText(DetailProfileActivity.this, "Logout Successful", Toast.LENGTH_LONG).show();
                startActivity(new Intent(DetailProfileActivity.this, MainActivity.class));
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
}
