package com.example.asifsabir.rideshareapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by asifsabir on 1/21/18.
 */

public class RiderRegistrationActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Button registerButton;
    EditText etName, etPhone, etPassword, etNid,etEmail;

    GPSTracker gps;
    double latOfSensor = 0, lonOfSensor = 0;
    String latitude = "", longitude = "";
    final Handler ha = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_registration);
        getSupportActionBar().setTitle("Rider Registration");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etName = (EditText) findViewById(R.id.et_fullName);
        etPassword = (EditText) findViewById(R.id.et_password_rider);
        etEmail = (EditText) findViewById(R.id.et_email_rider);
        etPhone = (EditText) findViewById(R.id.et_mobile);
        etNid = (EditText) findViewById(R.id.et_nid);
        registerButton = (Button) findViewById(R.id.button_register_rider);

        //getting gps data
        gps = new GPSTracker(RiderRegistrationActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation() && gps.getLatitude() != 0) {

            latOfSensor = gps.getLatitude();
            lonOfSensor = gps.getLongitude();

            //button enable kore dao

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName, password,phoneNumber,nid,email;

                fullName = etName.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                phoneNumber = etPhone.getText().toString().trim();
                nid = etNid.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                if (latOfSensor ==0) {
                    gps.showSettingsAlert();
                    Toast.makeText(getApplicationContext(), "Error getting location!", Toast.LENGTH_LONG).show();
                    Log.e("Error", "location error1");
                } else {
                    latitude = String.valueOf(latOfSensor);
                    longitude = String.valueOf(lonOfSensor);
                }


                if (fullName.equals("") || password.equals("") ||
                        phoneNumber.equals("") || nid.equals("") |
                        latitude.equals("") || longitude.equals("")) {

                    if (latitude.equals("") || longitude.equals("")) {
                        Toast.makeText(getApplicationContext(), "Error getting location!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Fill All Fields", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    DatabaseReference myRef = database.getReference("Rider").child(phoneNumber);
                    RiderReg rider = new RiderReg(fullName, password, phoneNumber,email,
                            nid,latitude, longitude,"5");
                    myRef.setValue(rider);
                    Toast.makeText(RiderRegistrationActivity.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RiderRegistrationActivity.this, LoginScreenActivity.class));
                    finish();

                }
            }
        });

    }



    @Override
    protected void onRestart() {
        super.onRestart();
        locationThread();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(this, LoginScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityIfNeeded(intent, 0);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void locationThread() {
        gps = new GPSTracker(RiderRegistrationActivity.this);

        ha.postDelayed(new Runnable() {

            @SuppressLint("NewApi")
            @Override
            public void run() {
                //call function
                if (gps.getLocation() == null) {
                    gps.showSettingsAlert();
                } else {

                    // check if GPS enabled
                    if (gps.canGetLocation() && gps.getLatitude() != 0) {
                        latOfSensor = gps.getLatitude();
                        lonOfSensor = gps.getLongitude();
                        Toast.makeText(getApplicationContext(), "Location found!", Toast.LENGTH_SHORT).show();

                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                    if (gps.getLatitude() == 0)
                        ha.postDelayed(this, 3000);
                }
            }

        }, 3000);

    }
}
