package com.example.bitmani.carbook;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterCarActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_OUTER = 1;

    private EditText ownerName;
    private EditText phoneNumber;

    private ProgressBar outerProgressBar;
    private Button chooseOuterImage;

    private ProgressBar circularProgressBar;
    private Button register;

    private Uri mImageUriOuter;
    private ImageView outerCheck;

    private DatabaseReference mDatabase;
    private StorageReference outerStorageRef;

    private Boolean outerSelected = false;
    private Boolean outerUploaded = false;
    private String outerImageurl;

    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);


        ownerName = findViewById(R.id.owner_name_id);
        phoneNumber = findViewById(R.id.phone_number_id);


        outerProgressBar = findViewById(R.id.progress_bar_outer_id);
        chooseOuterImage = findViewById(R.id.choose_image_outer_id);

        rootLayout = findViewById(R.id.registerConstraintLayout);
        circularProgressBar = findViewById(R.id.circular_progress_bar_id);


        outerCheck = findViewById(R.id.outer_image_check_id);

        register = findViewById(R.id.register_car_id);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        outerStorageRef = storage.getReference("RegisteredCars");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("RegisteredCars");


        Toolbar toolbar = findViewById(R.id.toolbar_register_car);
        toolbar.setTitle("Register Car");
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


        chooseOuterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooserOuter();
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCar();
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
                Toast.makeText(RegisterCarActivity.this, "loged out", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void openFileChooserOuter() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_OUTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_OUTER && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUriOuter = data.getData();
            outerCheck.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
            outerSelected = true;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFileOuter() {
        if (mImageUriOuter != null) {
            StorageReference fileReference = outerStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUriOuter));

            UploadTask uploadTask = fileReference.putFile(mImageUriOuter);

            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    outerImageurl = uri.toString();
                                    outerUploaded = true;

                                    String owner = ownerName.getText().toString();
                                    String phone = phoneNumber.getText().toString();
                                    FirebaseUser currentUser = GlobalDatas.currentUser;
                                    String email = currentUser.getEmail();
                                    CarRegisterData carRegisterData = new CarRegisterData(email, owner, phone, outerImageurl);
                                    String registerId = mDatabase.push().getKey();

                                    mDatabase.child(registerId).setValue(carRegisterData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            outerSelected = false;
                                            ownerName.setText("");
                                            phoneNumber.setText("");
                                            register.setVisibility(View.VISIBLE);
                                            circularProgressBar.setVisibility(View.GONE);
                                            outerCheck.clearColorFilter();
                                            Toast.makeText(RegisterCarActivity.this, "registered successfully", Toast.LENGTH_LONG).show();
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
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterCarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            outerProgressBar.setProgress((int) progress);
                        }
                    });

        } else {
            Toast.makeText(RegisterCarActivity.this, "file not chosen", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerCar() {
        String owner = ownerName.getText().toString();
        String phone = phoneNumber.getText().toString();

        if (owner.equals("") || phone.equals("")) {
            Toast.makeText(RegisterCarActivity.this, "fill all details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!outerSelected) {
            Toast.makeText(RegisterCarActivity.this, "file not chosen", Toast.LENGTH_SHORT).show();
        } else {
            register.setVisibility(View.GONE);
            circularProgressBar.setVisibility(View.VISIBLE);

            uploadFileOuter();

        }
    }

}
