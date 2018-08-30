package com.example.asifsabir.rideshareapp;

import android.*;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by asifsabir on 2/1/18.
 */


public class ShowDriverRequest extends AppCompatActivity implements OnMapReadyCallback {
    TextView tvRiderName, tvRiderPhone, tvRiderNid, tvRiderRating,
            tvDriverName, tvDriverPhone, tvDriverNid, tvDriverRating,
            tvDistance, tvFare, tvRideStatus;
    String riderName, riderPhone, riderRating, riderNid;
    String fromLat, fromLon, toLat, toLon;
    String driverName, driverPhone, driverRating, driverNid;
    String fare, distance, status;
    Button btnStartRide, btnEndRide, btnCancelRide, btnRateRider;
    RatingBar rateRider;
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_show_request);
        getSupportActionBar().setTitle("Show ride Request");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDriver);

        mapFragment.getMapAsync(this);

        tvRiderName = (TextView) findViewById(R.id.tv_driver_rider_name);
        tvRiderPhone = (TextView) findViewById(R.id.tv_driver_rider_phone);
        tvRiderNid = (TextView) findViewById(R.id.tv_driver_rider_nid);
        tvRiderRating = (TextView) findViewById(R.id.tv_driver_rider_rating);

        tvDriverName = (TextView) findViewById(R.id.tv_driver_driver_name);
        tvDriverPhone = (TextView) findViewById(R.id.tv_driver_driver_phone);
        tvDriverNid = (TextView) findViewById(R.id.tv_driver_driver_nid);
        tvDriverRating = (TextView) findViewById(R.id.tv_driver_driver_rating);

        tvDistance = (TextView) findViewById(R.id.tv_driver_distance);
        tvFare = (TextView) findViewById(R.id.tv_driver_fare);
        tvRideStatus = (TextView) findViewById(R.id.tv_driver_ride_status);

        btnStartRide = (Button) findViewById(R.id.btn_start_ride);
        btnEndRide = (Button) findViewById(R.id.btn_end_ride);
        btnCancelRide = (Button) findViewById(R.id.btn_cancel_ride);
        btnRateRider = (Button) findViewById(R.id.btn_rate_rider);

        rateRider = (RatingBar) findViewById(R.id.rt_bar_to_rider);


        SharedPreferences prefs = getSharedPreferences("driverData", MODE_PRIVATE);
        final int driverPhoneSaved = prefs.getInt("driverPhone", 0);

        final String reqKey = getIntent().getExtras().getString("reqKey", null);


        //fetching this drivers data

        final DatabaseReference DriverRef = FirebaseDatabase.getInstance().getReference("Driver").child(String.valueOf(driverPhoneSaved));
        DriverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                DriverReg driverReg = snapshot.getValue(DriverReg.class);
                driverName = driverReg.fullName;
                driverPhone = driverReg.mobile;
                driverNid = driverReg.nid;
                driverRating = driverReg.rating;

                //setting on TextView

                tvDriverName.setText(driverName);
                tvDriverPhone.setText(driverPhone);
                tvDriverNid.setText(driverNid);
                tvDriverRating.setText(driverRating);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowDriverRequest.this, "Error happened in fetching driver data!", Toast.LENGTH_SHORT).show();
            }

        });

        //fetching rides data (riders data)
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("rideRequest").child(reqKey);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                RideRequest rideRequest = snapshot.getValue(RideRequest.class);

                riderName = rideRequest.riderName;
                riderPhone = rideRequest.riderPhone;
                riderNid = rideRequest.riderNid;
                riderRating = rideRequest.riderRating;

                distance = rideRequest.distance;
                fare = rideRequest.fare;

                fromLat = rideRequest.fromLat;
                fromLon = rideRequest.fromLon;
                toLat = rideRequest.toLat;
                toLon = rideRequest.toLon;

                //setting on textview

                tvRiderName.setText(riderName);
                tvRiderPhone.setText(riderPhone);
                tvRiderNid.setText(riderNid);
                tvRiderRating.setText(riderRating);

                tvDistance.setText(rideRequest.distance + " Km");
                tvFare.setText(rideRequest.fare + " Tk.");

                //Map view working...
                //creating markers
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(Double.parseDouble(fromLat), Double.parseDouble(fromLon)));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_start))
                        .title("starting position");

                MarkerOptions markerOptions2 = new MarkerOptions();
                markerOptions2.position(new LatLng(Double.parseDouble(toLat), Double.parseDouble(toLon)));
                markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_stop))
                        .title("ending position");

                //add this marker to map
                mMap.addMarker(markerOptions);
                mMap.addMarker(markerOptions2);

                String url = getRequestUrl(new LatLng(Double.parseDouble(fromLat), Double.parseDouble(fromLon)),
                        new LatLng(Double.parseDouble(toLat), Double.parseDouble(toLon)));
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowDriverRequest.this, "Error happened in fetching ride data!", Toast.LENGTH_SHORT).show();
            }

        });


        btnStartRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancelRide.setVisibility(View.GONE);
                btnStartRide.setVisibility(View.GONE);
                btnEndRide.setVisibility(View.VISIBLE);
                Toast.makeText(ShowDriverRequest.this, "Ride Started", Toast.LENGTH_SHORT).show();

                //updating ride data with driver data
                DatabaseReference reqRef = FirebaseDatabase.getInstance().getReference("rideRequest").child(reqKey);

                RideRequest rideRequest = new RideRequest(riderName, riderPhone, riderNid, riderRating,
                        driverName, driverPhone, driverNid, driverRating,
                        distance, fare, "1", reqKey, fromLat, fromLon, toLat, toLon);
                reqRef.setValue(rideRequest);

                tvRideStatus.setText("Ride Started");
                tvRideStatus.setTextColor(Color.BLUE);
            }
        });

        btnEndRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //incrementing status
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("rideRequest").child(reqKey).child("rideStatus");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String count = snapshot.getValue(String.class);
                        int countNum = Integer.parseInt(count);
                        ++countNum;
                        rootRef.setValue(String.valueOf(countNum));


                        tvRideStatus.setText("Ride Ended");
                        tvRideStatus.setTextColor(Color.GREEN);
                        btnEndRide.setVisibility(View.GONE);

                        btnRateRider.setVisibility(View.VISIBLE);
                        rateRider.setVisibility(View.VISIBLE);
                        Toast.makeText(ShowDriverRequest.this, "Please rate the rider!", Toast.LENGTH_SHORT).show();

                        //enable rating button stuff
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ShowDriverRequest.this, "Error happened in fetching updating data!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowDriverRequest.this, "Ride Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        btnRateRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final float value = (rateRider.getRating() + Float.parseFloat(riderRating)) / 2;

                //updating rating
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Rider").child(riderPhone).child("rating");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String count = snapshot.getValue(String.class);
                        float countNum = Float.parseFloat(count);
                        countNum = (value + countNum) / 2;
                        rootRef.setValue(String.valueOf(countNum));
                        Toast.makeText(ShowDriverRequest.this, "Rider rated! \nThanks", Toast.LENGTH_LONG).show();
                        //sending data to rider activity

                        Intent i = new Intent(ShowDriverRequest.this, DriverMainActivity.class);
                        i.putExtra("driverPhone", driverPhone);
                        startActivity(i);
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ShowDriverRequest.this, "Error rating rider!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }


    //--------------.-.-.******************************************************************

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            //my location enabled has been cut
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//        Toast.makeText(this, str_org + "\n" + str_dest, Toast.LENGTH_SHORT).show();
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }


    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }


    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(7);

                polylineOptions.color(Color.GREEN);
                polylineOptions.width(7);


                polylineOptions.geodesic(true);

            }

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);

                //req second marker
                //making second marker
//
//                String url1 = getMapsApiDirectionsUrl1(listPoints.get(0), listPoints.get(1));
//                TaskRequestDirections taskRequestDirections1 = new TaskRequestDirections();
//                taskRequestDirections1.execute(url1);

            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
                tvDistance.setText("Error");
                tvFare.setText("Error");

            }
        }
    }

}
