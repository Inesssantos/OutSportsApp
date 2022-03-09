package com.example.outsports.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private double lat;
    private double lng;

    public Coordinates(LatLng location) {
        this.lat = location.latitude;
        this.lng = location.longitude;
    }

    public LatLng getLocation() {
        LatLng latLng = new LatLng(this.lat, this.lng);

        return latLng;
    }

    public void setLocation(LatLng location) {
        this.lat = location.latitude;
        this.lng = location.longitude;
    }
}
