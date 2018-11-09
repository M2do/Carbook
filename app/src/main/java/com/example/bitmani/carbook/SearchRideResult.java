package com.example.bitmani.carbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchRideResult extends AppCompatActivity {
    private static final String TAG = "SearchRideResult";

    private static final double DEFAULT_DOUBLE = 1.0;
    private static final int DEFAULT_INT = 1;

    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<String> mphone = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mFrom = new ArrayList<>();
    private ArrayList<String> mTo = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();

    ArrayList<OfferRideData> offerRideDataArray = new ArrayList<>();
    ArrayList<CarRegisterData> carRegisteredDataArray = new ArrayList<>();

    private LatitudeLongitude source;
    private LatitudeLongitude destination;
    private DateData dateData;

    private DatabaseReference mDatabase1;
    private DatabaseReference mDatabase2;


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


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase1 = database.getReference("OfferedRides");
        mDatabase2 = database.getReference("RegisteredCars");

        Toolbar toolbar = findViewById(R.id.toolbar_ride_search_result);
        toolbar.setTitle("Search Ride Result");
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

        getSearchResultList();
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

    private void getSearchResultList() {

        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    OfferRideData offerRideData = postSnapshot.getValue(OfferRideData.class);
                    offerRideDataArray.add(offerRideData);
                }
                getRegisteredCarDetails();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SearchRideResult.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase1.addValueEventListener(postListener);
    }

    private void getRegisteredCarDetails() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CarRegisterData carRegisterData = postSnapshot.getValue(CarRegisterData.class);
                    carRegisteredDataArray.add(carRegisterData);
                }

                filterForRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SearchRideResult.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase2.addValueEventListener(postListener);
    }

    private void filterForRecyclerView() {
        for (OfferRideData offerRideData : offerRideDataArray) {
            String email1 = offerRideData.getEmail();
            for (CarRegisterData carRegisterData : carRegisteredDataArray) {
                String email2 = carRegisterData.getEmail();
                if (email1.equals(email2)) {
                    PlaceDetails placeDetails = offerRideData.getSourcePlaceDetails();
                    double latitude = Double.parseDouble(placeDetails.getLatitude());
                    double longitude = Double.parseDouble(placeDetails.getLongitude());
                    LatitudeLongitude driverSource = new LatitudeLongitude(latitude, longitude);
                    if (CalculationByDistance(source, driverSource) <= 3.0) {
                        mName.add(carRegisterData.getOwnerName());
                        mphone.add(carRegisterData.getPhoneNumber());
                        mImages.add(carRegisterData.getOuterImageUrl());
                        mFrom.add(offerRideData.getSourcePlaceDetails().getPlacename());
                        mTo.add(offerRideData.getDestinationPlaceDetails().getPlacename());
                        mDate.add(offerRideData.getDate().getDayOfMonth() + ":" + offerRideData.getDate().getMonth() + ":" + offerRideData.getDate().getYear());
                        mTime.add(offerRideData.getTime().getHourOfDay() + ":" + offerRideData.getTime().getMinute());
                    }
                }
            }
        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.ride_search_recycler_view);

        SearchRideResultAdapter adapter = new SearchRideResultAdapter(this, mImages, mName, mphone, mFrom, mTo, mDate, mTime);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public double CalculationByDistance(LatitudeLongitude StartP, LatitudeLongitude EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.getLatitude();
        double lat2 = EndP.getLatitude();
        double lon1 = StartP.getLongitude();
        double lon2 = EndP.getLongitude();
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        /*
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        return Radius * c;
        */
        return km;
    }

}
