package com.example.asifsabir.rideshareapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by asifsabir on 2/1/18.
 */

public class ShowRiderRequest extends AppCompatActivity {
    TextView tvRiderName, tvRiderPhone, tvRiderNid, tvRiderRating,
            tvDriverName, tvDriverPhone, tvDriverNid, tvDriverRating,
            tvDistance, tvFare, tvRideStatus,
            tvWaitingTime;
    LinearLayout driverLayout, payLayout;
    Button btnRateDriver;
    RatingBar rateDriver;
    Button btnPay;
    final Handler ha = new Handler();
    GPSTracker gps;
    double time = 0; //in seconds
    double latOfSensor, lastSensor = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_show_request);
        getSupportActionBar().setTitle("My Ride Request");
        Spinner spinner = (Spinner) findViewById(R.id.spinner_bank_list);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bank_names_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Spinner spinnerAccType = (Spinner) findViewById(R.id.spinner_acc_type);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.acc_type_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerAccType.setAdapter(adapter2);

        tvWaitingTime = (TextView) findViewById(R.id.tv_waiting);
        payLayout = (LinearLayout) findViewById(R.id.pay_layout);
        rateDriver = (RatingBar) findViewById(R.id.rt_bar_to_driver);
        btnRateDriver = (Button) findViewById(R.id.btn_rate_driver);
        btnPay = (Button) findViewById(R.id.button_pay);
        driverLayout = (LinearLayout) findViewById(R.id.driver_layout);
        tvRiderName = (TextView) findViewById(R.id.tv_my_name);
        tvRiderPhone = (TextView) findViewById(R.id.tv_my_phone);
        tvRiderNid = (TextView) findViewById(R.id.tv_my_nid);
        tvRiderRating = (TextView) findViewById(R.id.tv_my_rating);

        tvDriverName = (TextView) findViewById(R.id.tv_my_driver_name);
        tvDriverPhone = (TextView) findViewById(R.id.tv_my_driver_phone);
        tvDriverNid = (TextView) findViewById(R.id.tv_my_driver_nid);
        tvDriverRating = (TextView) findViewById(R.id.tv_my_driver_rating);

        tvDistance = (TextView) findViewById(R.id.tv_my_distance);
        tvFare = (TextView) findViewById(R.id.tv_my_fare);
        tvRideStatus = (TextView) findViewById(R.id.tv_my_rider_status);


//rendering  ride data

        String reqKey = getIntent().getExtras().getString("reqKey", null);

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("rideRequest").child(reqKey);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                RideRequest rideRequest = snapshot.getValue(RideRequest.class);

                tvRiderName.setText(rideRequest.riderName);
                tvRiderPhone.setText(rideRequest.riderPhone);
                tvRiderNid.setText(rideRequest.riderNid);
                tvRiderRating.setText(rideRequest.riderRating);

                tvDriverName.setText(rideRequest.driverName);
                tvDriverPhone.setText(rideRequest.driverPhone);
                tvDriverNid.setText(rideRequest.driverNid);
                tvDriverRating.setText(rideRequest.driverRating);

                tvDistance.setText(rideRequest.distance + " Km");
                tvFare.setText(rideRequest.fare);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowRiderRequest.this, "Error happened in fetching ride data!", Toast.LENGTH_SHORT).show();
            }

        });


        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RideRequest rideRequest = dataSnapshot.getValue(RideRequest.class);

                tvRiderName.setText(rideRequest.riderName);
                tvRiderPhone.setText(rideRequest.riderPhone);
                tvRiderNid.setText(rideRequest.riderNid);
                tvRiderRating.setText(rideRequest.riderRating);

                tvDriverName.setText(rideRequest.driverName);
                tvDriverPhone.setText(rideRequest.driverPhone);
                tvDriverNid.setText(rideRequest.driverNid);
                tvDriverRating.setText(rideRequest.driverRating);

                tvDistance.setText(rideRequest.distance + " Km");
                tvFare.setText(rideRequest.fare);

                int status = Integer.parseInt(rideRequest.rideStatus);
                if (status == 1) {
                    tvDriverName.setText(rideRequest.driverName);
                    tvDriverPhone.setText(rideRequest.driverPhone);
                    tvDriverNid.setText(rideRequest.driverNid);
                    tvDriverRating.setText(rideRequest.driverRating);

                    //starting waiting time;
                    startWaitingTime();

                    tvRideStatus.setText("Ride Started");
                    tvRideStatus.setTextColor(Color.BLUE);
                    driverLayout.setVisibility(View.VISIBLE);
                }
                if (status == 2) {
                    tvRideStatus.setText("Ride Ended!");
                    tvRideStatus.setTextColor(Color.GREEN);

                    //removing waiting handler
                    ha.removeCallbacksAndMessages(null);
                    //updating payment
                    int fare1 = (int) (Integer.parseInt(tvFare.getText().toString()) + time * 5 / 60 / 1000); //tk 5 per min
                    tvWaitingTime.setText("final billing:" + fare1 + "tk");
                    Toast.makeText(ShowRiderRequest.this, "Bill adjusted", Toast.LENGTH_LONG).show();
                    tvFare.setText(fare1 + "(final)");

                    payLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(ShowRiderRequest.this, "Please pay!", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ShowRiderRequest.this, "Error happened in updating ride data!", Toast.LENGTH_SHORT).show();

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payLayout.setVisibility(View.GONE);
                btnRateDriver.setVisibility(View.VISIBLE);
                rateDriver.setVisibility(View.VISIBLE);
                //enable rating layout;
                Toast.makeText(ShowRiderRequest.this, "Please rate the driver", Toast.LENGTH_SHORT).show();
            }
        });


        btnRateDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final float value = (rateDriver.getRating() + Float.parseFloat(tvDriverRating.getText().toString())) / 2;

                //updating rating
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Driver")
                        .child(tvDriverPhone.getText().toString()).child("rating");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
//                        String count = snapshot.getValue(String.class);
//                        float countNum = Float.parseFloat(count);
                        rootRef.setValue(String.valueOf(value));
                        Toast.makeText(ShowRiderRequest.this, "Driver rated! \nThanks", Toast.LENGTH_LONG).show();
                        //sending data to rider activity

                        //sending data to rider activity

                        Intent i = new Intent(ShowRiderRequest.this, RiderMainAcitivity.class);
                        i.putExtra("riderName", tvRiderName.getText().toString());
                        i.putExtra("riderPhone", tvRiderPhone.getText().toString());
                        startActivity(i);
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ShowRiderRequest.this, "Error rating driver!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void startWaitingTime() {
        gps = new GPSTracker(ShowRiderRequest.this);
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
                        Log.e("log", "entered");
                        latOfSensor = gps.getLatitude();
                        Log.e("log", String.valueOf(latOfSensor));
                        if (latOfSensor == lastSensor) {
                            Log.e("log", "entered2");

                            Toast.makeText(ShowRiderRequest.this, "Waiting", Toast.LENGTH_SHORT).show();
                            time = time + 5000;
                            tvWaitingTime.setText("waiting: " + String.format("%.2f", time / 1000 / 60) + " minutes");
                        }
                        lastSensor = latOfSensor;
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                    ha.postDelayed(this, 5000);
                }
            }

        }, 5000);
    }
}
