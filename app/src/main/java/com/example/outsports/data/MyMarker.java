package com.example.outsports.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class MyMarker implements Serializable {

    private Coordinates coordinates;
    private String name;

    public MyMarker(String name, LatLng position) {
        this.name = name;
        this.coordinates = new Coordinates(position);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getCoordinates() {
        return coordinates.getLocation();
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = new Coordinates(coordinates);
    }
}
