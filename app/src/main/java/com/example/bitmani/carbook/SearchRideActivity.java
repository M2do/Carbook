package com.example.bitmani.carbook;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.util.Calendar;

public class SearchRideActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, DatePickerDialog.OnDateSetListener {
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;

    private Button chooseSource;
    private Button chooseDestination;
    private Button chooseDate;

    private String currentDate;

    private Button SearchButton;

    private ImageView fromCheck;
    private ImageView toCheck;
    private ImageView dateCheck;


    private PlaceDetails sourcePlaceDetails;
    private PlaceDetails destinationPlaceDetails;
    private DateData dateData;

    private boolean srcdst = true;

    private boolean fromChosen = false;
    private boolean toChosen = false;
    private boolean dateChosen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ride);

        chooseSource = findViewById(R.id.search_ride_from_button);
        chooseDestination = findViewById(R.id.search_ride_to_button);
        chooseDate = findViewById(R.id.search_ride_date_button);

        SearchButton = findViewById(R.id.search_ride_search_button);

        fromCheck = findViewById(R.id.search_ride_from_check);
        toCheck = findViewById(R.id.search_ride_to_check);
        dateCheck = findViewById(R.id.search_ride_date_check);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        chooseSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SearchRideActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        chooseDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                srcdst = false;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SearchRideActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "date picker");
            }
        });


        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromChosen && toChosen && dateChosen) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchRideActivity.this);
                    builder.setMessage("From : " + sourcePlaceDetails.getPlacename()
                            + "\n\nTo : " + destinationPlaceDetails.getPlacename()
                            + "\n\nDate : " + currentDate)
                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Search Ride", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), SearchRideResult.class);
                                    intent.putExtra("START_LATITUDE", Double.parseDouble(sourcePlaceDetails.getLatitude()));
                                    intent.putExtra("START_LONGITUDE", Double.parseDouble(sourcePlaceDetails.getLongitude()));

                                    intent.putExtra("END_LATITUDE", Double.parseDouble(destinationPlaceDetails.getLatitude()));
                                    intent.putExtra("END_LONGITUDE", Double.parseDouble(destinationPlaceDetails.getLongitude()));

                                    intent.putExtra("DATE_YEAR", dateData.getYear());
                                    intent.putExtra("DATE_MONTH", dateData.getMonth());
                                    intent.putExtra("DATE_DAY_OF_MONTH", dateData.getDayOfMonth());

                                    startActivity(intent);
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(SearchRideActivity.this, "fill all above details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_search_ride);
        toolbar.setTitle("Search Ride");
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
                Toast.makeText(SearchRideActivity.this, "loged out", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(SearchButton, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(SearchRideActivity.this, data);
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                PlaceDetails placeDetails = new PlaceDetails(placename, latitude, longitude, address);

                if (srcdst) {
                    sourcePlaceDetails = placeDetails;
                    fromCheck.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                    fromChosen = true;
                } else {
                    destinationPlaceDetails = placeDetails;
                    toCheck.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                    toChosen = true;
                }

                srcdst = true;
            }
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        dateCheck.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        dateData = new DateData(year, month, dayOfMonth);
        dateChosen = true;
    }
}
