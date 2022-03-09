package com.example.outsports.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Activity implements Serializable {

    private final int id;
    private final List<Coordinates> route;
    private final List<MyMarker> pointsofinterest;
    private double time;
    private float distance;
    private int calories;
    private Calendar date;
    private boolean group;

    public Activity() {
        id = hashCode();
        time = 0;
        distance = 0.0f;
        calories = 0;
        route = new ArrayList<>();
        date = Calendar.getInstance();
        group = false;
        pointsofinterest = new ArrayList<>();
    }

    //////////////// GETS BEGIN ////////////////
    public int getId() {
        return id;
    }

    public double getTime() {
        return time;
    }

    //////////////// SETS BEGIN ////////////////
    public void setTime(double time) {
        this.time = time;
    }

    public String getTimeString() {
        int h = (int) (time / 3600000);
        int m = (int) (time - h * 3600000) / 60000;
        int s = (int) (time - h * 3600000 - m * 60000) / 1000;
        return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public List<LatLng> getRoute() {
        List<LatLng> latLngList = new ArrayList<>();

        for (Coordinates value : route) {
            latLngList.add(value.getLocation());
        }

        return latLngList;
    }

    ///////////// GETS END ///////////////////

    public void setRoute(List<LatLng> route) {
        for (LatLng value : route) {
            this.route.add(new Coordinates(value));
        }
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public boolean getGroup() {
        return group;
    }

    public List<MarkerOptions> getPointsofinterest() {
        List<MarkerOptions> points = new ArrayList<>();

        for (MyMarker value : pointsofinterest) {
            points.add(new MarkerOptions().position(value.getCoordinates()).title(value.getName()).snippet(value.getCoordinates().toString()));
        }
        return points;
    }

    public void setPointsofinterest(List<Marker> points) {
        for (Marker value : points) {
            pointsofinterest.add(new MyMarker(value.getTitle(), value.getPosition()));
        }
    }

    public void setGroup() {
        group = true;
    }

    ///////////// SETS END ///////////////////

}
