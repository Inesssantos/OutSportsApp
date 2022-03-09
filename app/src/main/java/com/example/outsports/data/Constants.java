package com.example.outsports.data;

import android.graphics.Color;
import android.os.Build;

import java.util.HashMap;

public interface Constants {

    float CONVERSION_CM_FT = (float) 0.032808;
    float CONVERSION_KG_LB = (float) 2.2046;
    float CONVERSION_KM_MI = (float) 0.62137;

    float MINIMUM_DISTANCE_KM = 0.100f;
    float MAXIMUM_DISTANCE_KM = 1f;

    float MINIMUM_DISTANCE_MI = 0.0621f;
    float MAXIMUM_DISTANCE_MI = 0.6213f;

    HashMap<Integer, Double[]> WEIGHT_STATUS_VALUES = new HashMap<Integer, Double[]>() {
        {
            put(1, new Double[]{0.0, 18.5});
            put(2, new Double[]{18.5, 24.9});
            put(3, new Double[]{24.9, 29.9});
            put(4, new Double[]{29.9, 34.9});
            put(5, new Double[]{34.9, 100.0});
        }
    };

    HashMap<Integer, Integer> WEIGHT_STATUS_COLORS = new HashMap<Integer, Integer>() {
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                put(0, Color.parseColor("#3896E1"));
                put(1, Color.parseColor("#3ABF00"));
                put(2, Color.parseColor("#FFE714"));
                put(3, Color.parseColor("#FF8C00"));
                put(4, Color.parseColor("#DA5454"));
            }
        }
    };
}
