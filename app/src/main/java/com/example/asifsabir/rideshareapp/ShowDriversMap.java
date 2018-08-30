package com.example.asifsabir.rideshareapp;

/**
 * Created by asifsabir on 12/20/17.
 */


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowDriversMap extends AppCompatActivity implements OnMapReadyCallback {
    public DatabaseReference databaseDriverReference;
    public GoogleMap mMap;
    GPSTracker gps;
    double latMy, lonMy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_drivers_map);
        getSupportActionBar().setTitle("Map View of Drivers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //getting gps data
        gps = new GPSTracker(ShowDriversMap.this);

        // check if GPS enabled
        if (gps.canGetLocation() && gps.getLatitude() != 0) {

            latMy = gps.getLatitude();
            lonMy = gps.getLongitude();

            //button enable kore dao

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device.
     * This method will only be triggered once the user has installed
     * Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;


        databaseDriverReference = FirebaseDatabase.getInstance().getReference("Driver");

        databaseDriverReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String driverName = dataSnapshot.child("fullName").getValue().toString();
                    String driverMobile = dataSnapshot.child("mobile").getValue().toString();
                    String driverVehicleType = dataSnapshot.child("vehicleType").getValue().toString();
                    String driverRegNo = dataSnapshot.child("regNo").getValue().toString();
                    String driverRating = dataSnapshot.child("rating").getValue().toString();


                    double latitude = Double.parseDouble(dataSnapshot.child("lat").getValue(String.class).toString());
                    double longitude = Double.parseDouble(dataSnapshot.child("lon").getValue(String.class).toString());

                    if (driverVehicleType.equals("Bike")) {
                        mMap.addMarker(new MarkerOptions()
                                .title(driverVehicleType)
                                .snippet("Driver name:" + driverName)
                                .visible(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))
                                .position(new LatLng(
                                        latitude,
                                        longitude
                                ))
                        );
                    } else {
                        mMap.addMarker(new MarkerOptions()
                                .title(driverVehicleType)
                                .snippet("Driver name:" + driverName)
                                .visible(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                                .position(new LatLng(
                                        latitude,
                                        longitude
                                ))
                        );
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
//your position
        mMap = googleMap;
        LatLng YourPosition = new LatLng(Double.parseDouble(String.valueOf(latMy)), Double.parseDouble(String.valueOf(lonMy)));
        Marker mMarker = googleMap.addMarker(new MarkerOptions()
                .position(YourPosition).snippet("this is you.")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
                .title("You!"));


        googleMap.addCircle(new CircleOptions()
                .center(YourPosition)
                .radius(500)
                .strokeWidth(2)
                .strokeColor(Color.RED)
                .fillColor(0x25FF0000));  //0x :hexa code ; 60 is % ; last 6 digit is color code

        mMarker.showInfoWindow();
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(YourPosition, 17);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(YourPosition));
        googleMap.animateCamera(yourLocation);

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(this, RiderMainAcitivity.class);
                intent.putExtra("riderName", getIntent().getExtras().getString("riderName", null))
                        .putExtra("riderPhone", getIntent().getExtras().getString("riderPhone", null));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityIfNeeded(intent, 0);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(ShowDriversMap.this, RiderMainAcitivity.class)
                .putExtra("riderName", getIntent().getExtras().getString("riderName", null))
                .putExtra("riderPhone", getIntent().getExtras().getString("riderPhone", null)));
        finish();
    }

}
