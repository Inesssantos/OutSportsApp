package com.example.outsports.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Calendar;

public class User implements Serializable {

    private final int id;
    private String name;
    private String email;
    //////////////////////////////
    private Calendar birthday;
    //////////////////////////////
    private Float weight;
    private Float height;
    //////////////////////////////
    private String language;
    private String units_height;
    private String units_weight;
    private String units_distance;
    //////////////////////////////
    private int color;
    private LatLng last_location;
    //////////////////////////////

    public User(String name, String email, Calendar birthday, Float weight, Float height, String language, String units_height, String units_weight, String units_distance) {
        this.id = hashCode();
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.weight = weight;
        this.height = height;
        this.language = language;
        this.units_height = units_height;
        this.units_weight = units_weight;
        this.units_distance = units_distance;

        this.color = 0;
    }

    //////////////// GETS BEGIN ////////////////
    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    //////////////// SETS BEGIN ////////////////
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public String getLanguage() {
        return language;
    }
    //////////////// GETS END ////////////////

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUnits_height() {
        return units_height;
    }

    public void setUnits_height(String units_height) {
        this.units_height = units_height;
    }

    public String getUnits_weight() {
        return units_weight;
    }

    public void setUnits_weight(String units_weight) {
        this.units_weight = units_weight;
    }

    public String getUnits_distance() {
        return units_distance;
    }

    public void setUnits_distance(String units_distance) {
        this.units_distance = units_distance;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public LatLng getLast_location() {
        return last_location;
    }

    public void setLast_location(LatLng last_location) {
        this.last_location = last_location;
    }
    //////////////// SETS END ////////////////
}
