package com.example.outsports.data;

import android.graphics.Color;

import java.util.Calendar;
import java.util.Random;

public class CreateData {
    private final Group group;
    private ListActivities activities;

    public CreateData() {
        activities = new ListActivities();
        group = new Group();
    }

    public ListActivities get_Activities() {
        activities = new ListActivities();

        Calendar date = Calendar.getInstance();

        for (int index = 0; index < 11; index++) {

            Activity activity = new Activity();
            activity.setDistance(8780);
            activity.setTime(4548000);
            activity.setCalories(687);
            activity.setDate(date);
            activities.addActivity(activity);

        }

        return activities;
    }

    public Group get_Group(User owner) {
        Random rnd = new Random();

        owner.setName("Owner");

        group.setName("Trail");

        group.setDistanceUnits(owner.getUnits_distance());

        owner.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));

        group.setOwner(owner);
        group.addUser(owner);

        Calendar birthday = Calendar.getInstance();
        birthday.set(Calendar.YEAR, 2014);

        for (int i = 1; i < 4; i++) {
            String name = "Test " + i;
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            User user = new User(name, "a@a.a", birthday, 70f, 170f, "en", "cm", "kg", "km");
            user.setColor(color);
            group.addUser(user);
        }

        return group;
    }
}
