package com.example.asifsabir.rideshareapp;

/**
 * Created by asifsabir on 1/22/18.
 */

public class DriverReg {
    public String fullName;
    public String email;
    public String password;
    public String mobile;
    public String age;
    public String nid;
    public String regNo;
    public String vehicleType;
    public String lat;
    public String lon;
    public String rating;

    public DriverReg() {
        // Default constructor required for calls to DataSnapshot.getValue(Article.class)
    }

    public DriverReg(String fullName, String email, String password,
                     String mobile, String age, String nid,
                     String regNo, String vehicleType, String lat,
                     String lon, String rating) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.age = age;
        this.nid = nid;
        this.regNo = regNo;
        this.vehicleType = vehicleType;
        this.lat = lat;
        this.lon = lon;
        this.rating = rating;
    }
}

