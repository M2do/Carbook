package com.example.bitmani.carbook;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class OfferRideActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private Button chooseSource;
    private Button chooseDestination;
    private Button chooseDate;
    private Button chooseTime;
    private Button confirmButton;
    private View rootLayout;

    private ImageView fromCheck;
    private ImageView toCheck;
    private ImageView dateCheck;
    private ImageView timeCheck;

    private PlaceDetails sourcePlaceDetails;
    private PlaceDetails destinationPlaceDetails;
    private DateData dateData;
    private TimeData timeData;
    private DatabaseReference mDatabase;


    private String currentDate;
    private String currentTime;

    private boolean srcdst = true;

    private boolean fromChosen = false;
    private boolean toChosen = false;
    private boolean dateChosen = false;
    private boolean timeChosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);

        chooseSource = findViewById(R.id.source_details_id);
        chooseDestination = findViewById(R.id.destination_details_id);
        chooseDate = findViewById(R.id.date_picker_id);
        chooseTime = findViewById(R.id.time_picker_id);
        confirmButton = findViewById(R.id.confirm_button_id);
        rootLayout = findViewById(R.id.constraintLayout);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();

        fromCheck = findViewById(R.id.from_check_id);
        toCheck = findViewById(R.id.to_check_id);
        dateCheck = findViewById(R.id.date_check_id);
        timeCheck = findViewById(R.id.time_check_id);

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
                    startActivityForResult(builder.build(OfferRideActivity.this), PLACE_PICKER_REQUEST);
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
                    startActivityForResult(builder.build(OfferRideActivity.this), PLACE_PICKER_REQUEST);
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


        chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timepicker = new TimePickerFragment();
                timepicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromChosen && toChosen && dateChosen && timeChosen) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OfferRideActivity.this);
                    builder.setMessage("From : " + sourcePlaceDetails.getPlacename()
                                        + "\n\nTo : " + destinationPlaceDetails.getPlacename()
                                        + "\n\nDate : " + currentDate
                                        +"\n\nTime : " + currentTime)
                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseUser currentUser = GlobalDatas.currentUser;
                                    String email = currentUser.getEmail();
                                    OfferRideData offerRideData = new OfferRideData(email, sourcePlaceDetails, destinationPlaceDetails, dateData, timeData);
                                    mDatabase.child("OfferedRides").setValue(offerRideData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(OfferRideActivity.this, "You offered a ride", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), OfferRideConfirmed.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar snackbar = Snackbar.make(rootLayout, "Connection Error", Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                        }
                                    });

                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(OfferRideActivity.this, "fill all above details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_offer_ride);
        toolbar.setTitle("Offer Ride");
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
                Toast.makeText(OfferRideActivity.this, "logged out", Toast.LENGTH_SHORT).show();
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
        Snackbar.make(confirmButton, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(OfferRideActivity.this, data);
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

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeCheck.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        currentTime = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);

        timeData = new TimeData(hourOfDay, minute);
        timeChosen = true;
    }
}
