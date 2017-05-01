package com.example.taitran.hearthopehelp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.login.LoginManager;

public class BugReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);
        Spinner bugOption = (Spinner) findViewById(R.id.brpSpinner);
        String[] items = new String[]{"Issue with login using Facebook", "Issue with maps", "Issue with viewing profiles"
        ,"Other"};
        bugOption.setPrompt("Select one option");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(BugReport.this, R.layout.support_simple_spinner_dropdown_item ,items);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        bugOption.setAdapter(adapter);

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
                Toast.makeText(BugReport.this, "Logout Successful", Toast.LENGTH_LONG).show();
                startActivity(new Intent(BugReport.this, MainActivity.class));
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
