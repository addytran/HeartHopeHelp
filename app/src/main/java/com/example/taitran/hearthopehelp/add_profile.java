package com.example.taitran.hearthopehelp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;

public class add_profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.hhh);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_add_profile);
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
}
