package com.example.asifsabir.rideshareapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by asifsabir on 1/21/18.
 */

public class DriverMainActivity extends AppCompatActivity {
    TextView tvFullName, tvEmail, tvMobile, tvAge, tvNid, tvRegistraion, tvType;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        FirebaseMessaging.getInstance().subscribeToTopic("driver");

        tvFullName = (TextView) findViewById(R.id.tv_driver_name);
        tvEmail = (TextView) findViewById(R.id.tv_driver_email);
        tvMobile = (TextView) findViewById(R.id.tv_driver_mobile);
        tvAge = (TextView) findViewById(R.id.tv_driver_age);
        tvNid = (TextView) findViewById(R.id.tv_driver_nid);
        tvRegistraion = (TextView) findViewById(R.id.tv_driver_registration);
        tvType = (TextView) findViewById(R.id.tv_driver_vehicle_type);
        ratingBar = (RatingBar) findViewById(R.id.rt_bar);
//retrieving phone data
        String driverPhone = getIntent().getExtras().getString("driverPhone", null);

        //saving driver data
        SharedPreferences.Editor editor = getSharedPreferences("driverData", MODE_PRIVATE).edit();
        editor.putInt("driverPhone",Integer.parseInt(driverPhone));
        editor.apply();
//rendering  driver data


        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Driver").child(driverPhone);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                DriverReg driverReg = snapshot.getValue(DriverReg.class);

                tvFullName.setText(driverReg.fullName);
                tvEmail.setText(driverReg.email);
                tvMobile.setText(driverReg.mobile);
                tvAge.setText(driverReg.age);
                tvNid.setText(driverReg.nid);
                tvRegistraion.setText(driverReg.regNo);
                tvType.setText(driverReg.vehicleType);
                ratingBar.setRating(Float.parseFloat(driverReg.rating));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DriverMainActivity.this, "Error happened in fetching user data!", Toast.LENGTH_SHORT).show();
            }

        });


    }


}