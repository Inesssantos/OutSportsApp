package com.example.outsports.ui.statistics_months;

import com.example.outsports.data.Activity;

import java.util.List;

public class ResumeMonth {

    private final String months;
    private final int total_activities;
    private final float total_distance;
    private final List<Activity> activities;

    public ResumeMonth(String months, int total_activities, float total_distance, List<Activity> resumeActivities) {
        this.months = months;
        this.total_activities = total_activities;
        this.total_distance = total_distance;
        this.activities = resumeActivities;
    }

    public String getMonth() {
        return months;
    }

    public int getTotal_activities() {
        return total_activities;
    }

    public float getTotal_distance() {
        return total_distance;
    }

    public Activity getActivities(int index) {
        return activities.get(index);
    }

    public int getActivitiesSize() {
        if (activities == null) {
            return 0;
        } else {
            return activities.size();
        }
    }

}
