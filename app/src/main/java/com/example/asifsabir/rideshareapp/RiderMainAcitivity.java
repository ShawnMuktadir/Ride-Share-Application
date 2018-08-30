package com.example.asifsabir.rideshareapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asifsabir on 1/21/18.
 */

public class RiderMainAcitivity extends AppCompatActivity {
    TextView tvRiderName, tvRiderMobile;
    Button btnDriversMap, btnSendReq;

    DatabaseReference databaseReference;


    ProgressDialog progressDialog;

    List<DriverReg> list = new ArrayList<>();

    RecyclerView recyclerView;

    RecyclerView.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_main);
        tvRiderName = (TextView) findViewById(R.id.tv_rider_name);
        tvRiderMobile = (TextView) findViewById(R.id.tv_rider_mobile);
        btnDriversMap = (Button) findViewById(R.id.button_see_map);
        btnSendReq = (Button) findViewById(R.id.button_request_ride);
        //retrieving phone data
        final String riderName = getIntent().getExtras().getString("riderName", null);
        final String riderPhone = getIntent().getExtras().getString("riderPhone", null);


        tvRiderName.setText(riderName);
        tvRiderMobile.setText(riderPhone);

        btnDriversMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RiderMainAcitivity.this, ShowDriversMap.class);
                i.putExtra("riderName", riderName);
                i.putExtra("riderPhone", riderPhone);
                startActivity(i);
                finish();
            }
        });
        btnSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RiderMainAcitivity.this, MapsActivity.class);
                i.putExtra("riderName", riderName);
                i.putExtra("riderPhone", riderPhone);
                startActivity(i);
                finish();
            }
        });


        //showing list view

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(RiderMainAcitivity.this));

        progressDialog = new ProgressDialog(RiderMainAcitivity.this);

        progressDialog.setMessage("Loading Data... \n Please wait!");

        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("Driver");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                    DriverReg studentDetails = dataSnapshot1.getValue(DriverReg.class);

                    list.add(studentDetails);
                }


                adapter = new RecyclerViewAdapter(RiderMainAcitivity.this, list);

                recyclerView.setAdapter(adapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }
}