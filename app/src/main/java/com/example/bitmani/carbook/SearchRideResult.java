package com.example.bitmani.carbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SearchRideResult extends AppCompatActivity {
    private static final double DEFAULT_DOUBLE = 1.0;
    private static final int DEFAULT_INT = 1;


    private LatitudeLongitude source;
    private LatitudeLongitude destination;
    private DateData dateData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ride_result);


        Intent intent = getIntent();
        double startLatitude = intent.getDoubleExtra("START_LATITUDE", DEFAULT_DOUBLE);
        double startLongitude = intent.getDoubleExtra("START_LONGITUDE", DEFAULT_DOUBLE);
        source = new LatitudeLongitude(startLatitude, startLongitude);

        double endLatitude = intent.getDoubleExtra("END_LATITUDE", DEFAULT_DOUBLE);
        double endLongitude = intent.getDoubleExtra("END_LONGITUDE", DEFAULT_DOUBLE);
        destination = new LatitudeLongitude(endLatitude, endLongitude);


        int year = intent.getIntExtra("DATE_YEAR", DEFAULT_INT);
        int month = intent.getIntExtra("DATE_MONTH", DEFAULT_INT);
        int dayOfMonth = intent.getIntExtra("DATE_DAY_OF_MONTH", DEFAULT_INT);
        dateData = new DateData(year, month, dayOfMonth);


        Toolbar toolbar = findViewById(R.id.toolbar_ride_search_result);
        toolbar.setTitle("Ride Search Result");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.toolbar_home:
                intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                break;
            case R.id.toolbar_logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(SearchRideResult.this, "loged out", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
