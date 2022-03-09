package com.example.outsports.ui.activity_tracking;

public class CaloriesBurned {

    private final double weight;
    private final String weight_units;
    private final String distance_units;

    public CaloriesBurned(double weight, String weight_units, String distance_units) {
        this.weight = weight;
        this.weight_units = weight_units;
        this.distance_units = distance_units;
    }

    public double calculateAveragePace(double minutes, double distance) {
        double pace = 0;

        if (minutes != 0 && distance != 0) {
            pace = (minutes / distance);
            double seconds = (pace % 1) * 60;

            double value1 = ((int) Math.floor(pace));
            double value2 = (seconds / 100);
            double result = value1 + value2;
            return result;
        }

        return 0;
    }

    public String calculateAveragePaceString(double minutes, double distance) {
        double pace = 0;

        if (minutes != 0 && distance != 0) {
            pace = (minutes / distance);
            double seconds = (pace % 1) * 60;
            String result = (int) Math.floor(pace) + ":" + (int) Math.round(seconds) + "min/" + distance_units;
            return result;
        }

        return "0:0min/" + distance_units;
    }


    public String calculateCaloriesBurnedString(double minutes, double distance) {
        double c = 0;
        double met = 0;

        met = getMetByUnknownSpeed(minutes, distance);

        if (weight_units.equals("lb")) {
            c = cclb(weight, met, minutes);
        } else {
            c = cckg(weight, met, minutes);
        }

        return c + "kcla";
    }

    public double calculateCaloriesBurned(double minutes, double distance) {
        double c = 0;
        double met = 0;

        met = getMetByUnknownSpeed(minutes, distance);

        if (weight_units.equals("lb")) {
            c = cclb(weight, met, minutes);
        } else {
            c = cckg(weight, met, minutes);
        }

        return c;
    }

    private double getMetByUnknownSpeed(double minutes, double distance) {

        double met = -1;

        double speed;
        double[] speed_deflauts_values = {4, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 10, 11, 12, 13};
        double[] met_values = {6, 8.3, 9, 9.8, 10.5, 11, 11.4, 11.8, 12.3, 12.8, 14.5, 16, 19, 19.8};

        if (distance_units.equals("km")) {
            speed = closestSpeed((distance / minutes) * 60 * (0.621371192f));
        } else {
            speed = closestSpeed(((distance / minutes) * 60));
        }

        int index = 0;
        for (double value : speed_deflauts_values) {
            if (speed == value) {
                met = met_values[index];
                break;
            }
            index++;
        }

        if (met == -1) {
            return 23;
        } else {
            return met;
        }

    }

    private double closestSpeed(double num) {
        double[] knownSpeeds = {4, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 10, 11, 12, 13, 14};
        double curr = knownSpeeds[0];
        double diff = Math.abs(num - curr);
        for (int val = 0; val < knownSpeeds.length; val++) {
            double newdiff = Math.abs(num - knownSpeeds[val]);
            if (newdiff < diff) {
                diff = newdiff;
                curr = knownSpeeds[val];
            }
        }
        return curr;
    }


    private double cckg(double kg, double met, double mins) {
        return Math.round(met * 3.5 * (kg) / 200 * mins);
    }

    private double cclb(double lb, double met, double mins) {
        return cckg((double) (lb * 0.45359237), met, mins);
    }

}
