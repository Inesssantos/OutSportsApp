package com.example.outsports.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {

    private final List<User> groupUsers;
    private int id;
    private String name;
    private float distance;
    private String distance_units;
    private User owner;

    public Group(String name, int distance, User owner) {
        this.id = hashCode();
        this.name = name;
        this.distance = distance;
        this.owner = owner;
        groupUsers = new ArrayList<>();
    }

    public Group() {
        groupUsers = new ArrayList<>();
    }

    //////////////// GETS BEGIN ////////////////
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //////////////// SETS BEGIN ////////////////
    public void setName(String name) {
        this.name = name;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getDistanceUnits() {
        return distance_units;
    }

    public void setDistanceUnits(String units) {
        distance_units = units;
    }

    public User getOwner() {
        return owner;
    }
    //////////////// GETS END ////////////////

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getGroupUsers() {
        return groupUsers;
    }

    public String[] getListNameUsers() {
        String[] names = new String[]{};
        int index = 0;
        for (User value : groupUsers) {
            names[index] = value.getName();
        }
        return names;
    }

    public LatLng getUserLatLng(String name) {
        for (User value : groupUsers) {
            if (value.getName().equals(name)) {
                return value.getLast_location();
            }
        }
        return null;
    }

    public void addUser(User user) {
        groupUsers.add(user);
    }

    public void removeUser(int id) {
        int index = 1;
        for (User value : groupUsers) {
            if (value.getID() == id) {
                groupUsers.remove(0);
            }
            index++;
        }
    }

    public void setLatLngUser(int id, LatLng latLng) {
        for (User value : groupUsers) {
            if (value.getID() == id) {
                value.setLast_location(latLng);
            }
        }
    }
    ///////////// SETS END ///////////////////
}
