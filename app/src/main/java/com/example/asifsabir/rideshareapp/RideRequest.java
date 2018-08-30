package com.example.asifsabir.rideshareapp;

/**
 * Created by asifsabir on 2/1/18.
 */

public class RideRequest {
    public String riderName;
    public String riderPhone;
    public String riderNid;
    public String riderRating;

    public String driverName;
    public String driverPhone;
    public String driverNid;
    public String driverRating;

    public String distance;
    public String fare;
    public String rideStatus;
    public String key;

    public String fromLat;
    public String fromLon;
    public String toLat;
    public String toLon;


    public RideRequest() {
        //Default constructor for parsing datasnapshot
    }

    public RideRequest(String riderName, String riderPhone, String riderNid, String riderRating,
                       String driverName, String driverPhone, String driverNid, String driverRating,
                       String distance, String fare, String rideStatus,String key,
                       String fromLat,String fromLon,String toLat,String toLon) {

        this.riderName = riderName;
        this.riderPhone = riderPhone;
        this.riderNid = riderNid;
        this.riderRating = riderRating;

        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.driverNid = driverNid;
        this.driverRating = driverRating;

        this.distance = distance;
        this.fare = fare;
        this.rideStatus = rideStatus;
        this.key = key;

        this.fromLat=fromLat;
        this.fromLon=fromLon;
        this.toLat=toLat;
        this.toLon=toLon;
    }

}
