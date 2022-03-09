package com.example.outsports.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListActivities implements Serializable {

    private final List<Activity> activities;

    public ListActivities() {
        activities = new ArrayList<>();
    }

    ///////////// GETS END ///////////////////
    public int totalActivitiesYear(int year) {
        int index = 0;
        for (Activity value : activities) {
            if (year == value.getDate().get(Calendar.YEAR)) {
                index++;
            }
        }
        return index;
    }

    public int totalActivitiesMonth(int month) {
        int index = 0;
        for (Activity value : activities) {
            if (month == value.getDate().get(Calendar.MONTH)) {
                index++;
            }
        }
        return index;
    }

    public float totalDistanceMonth(int month) {
        float total = 0;
        for (Activity value : activities) {
            if (month == value.getDate().get(Calendar.MONTH)) {
                total += value.getDistance();
            }
        }
        return total;
    }

    public float totalDistanceYear(int year) {
        float total = 0;
        for (Activity value : activities) {
            if (year == value.getDate().get(Calendar.YEAR)) {
                total += value.getDistance();
            }
        }
        return total;
    }

    public float totalTimeYear(int year) {
        float total = 0;
        for (Activity value : activities) {
            if (year == value.getDate().get(Calendar.YEAR)) {
                total += value.getTime();
            }
        }
        return total;
    }


    public List<Activity> getActivitiesMonth(int month) {
        List<Activity> monthsactivities = new ArrayList<>();
        for (Activity value : activities) {
            if (value.getDate().get(Calendar.MONTH) == month) {
                monthsactivities.add(value);
            }
        }

        return monthsactivities;
    }

    public List<Activity> getLastActivities() {
        List<Activity> lastactivities = new ArrayList<>();
        if (activities == null || activities.size() == 0) {
            return null;
        } else {
            int size = activities.size() - 1;
            for (int i = 0; i < 4; i++) {
                if (size - i < 0) {
                    break;
                } else {
                    lastactivities.add(activities.get(size - i));
                }
            }

            return lastactivities;
        }
    }

    ///////////// GETS END ///////////////////

    //////////////// SETS BEGIN ////////////////
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity(int id) {
        int index = 0;
        for (Activity value : activities) {
            if (value.getId() == id) {
                activities.remove(index);
                break;
            }
            index++;
        }
    }
    ///////////// SETS END ///////////////////

}
