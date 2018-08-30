package com.example.asifsabir.rideshareapp;

/**
 * Created by asifsabir on 1/21/18.
 */


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverRegistrationActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Button registerButton;
    EditText etName, etPhone, etEmail, etPassword, etAge, etNid, etVechicleNum;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    GPSTracker gps;
    double latOfSensor = 0, lonOfSensor = 0;
    String latitude = "", longitude = "";
    final Handler ha = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);
        getSupportActionBar().setTitle("Driver Registration");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etName = (EditText) findViewById(R.id.et_fullName);
        etPassword = (EditText) findViewById(R.id.et_password);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPhone = (EditText) findViewById(R.id.et_mobile);
        etAge = (EditText) findViewById(R.id.et_age);
        etNid = (EditText) findViewById(R.id.et_nid);
        etVechicleNum = (EditText) findViewById(R.id.et_vehicle_no);


        radioGroup = (RadioGroup) findViewById(R.id.radio);
        registerButton = (Button) findViewById(R.id.button_register_driver);

        //getting gps data
        gps = new GPSTracker(DriverRegistrationActivity.this);

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
                String fullName, password, email, phoneNumber, nid, age, vehicleNumber;

                fullName = etName.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                phoneNumber = etPhone.getText().toString().trim();
                nid = etNid.getText().toString().trim();
                age = etAge.getText().toString().trim();
                vehicleNumber = etVechicleNum.getText().toString().trim();

                if (latOfSensor ==0) {
                    gps.showSettingsAlert();
                    Toast.makeText(getApplicationContext(), "Error getting location!", Toast.LENGTH_LONG).show();
                    Log.e("Error", "location error1");
                } else {
                    latitude = String.valueOf(latOfSensor);
                    longitude = String.valueOf(lonOfSensor);
                }

                // get selected radio button from radioGroup
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton = (RadioButton) findViewById(selectedId);

                String vehicleType = radioButton.getText().toString();



                if (fullName.equals("") || password.equals("") || email.equals("") ||
                        phoneNumber.equals("") || nid.equals("") ||
                        age.equals("") || vehicleNumber.equals("") ||
                        latitude.equals("") || longitude.equals("")) {

                    if (latitude.equals("") || longitude.equals("")) {
                        Toast.makeText(getApplicationContext(), "Error getting location!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Fill All Fields", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    DatabaseReference myRef = database.getReference("Driver").child(phoneNumber);
                    DriverReg driver = new DriverReg(fullName, email, password, phoneNumber, age,
                            nid, vehicleNumber, vehicleType,
                            latitude, longitude, "5");
                    myRef.setValue(driver);
                    Toast.makeText(DriverRegistrationActivity.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DriverRegistrationActivity.this, LoginScreenActivity.class));
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
        gps = new GPSTracker(DriverRegistrationActivity.this);

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
