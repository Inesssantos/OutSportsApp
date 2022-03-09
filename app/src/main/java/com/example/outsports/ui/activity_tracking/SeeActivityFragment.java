package com.example.outsports.ui.activity_tracking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.outsports.R;
import com.example.outsports.data.Activity;
import com.example.outsports.data.Conversion;
import com.example.outsports.data.InternalStorage;
import com.example.outsports.data.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class SeeActivityFragment extends Fragment implements
        OnMapReadyCallback {

    private static final int REQUEST_FINE_LOCATION = 44;
    private TextView tv_distance, tv_time, tv_calories, tv_average_pace, tv_date;
    private User user;
    private Activity activity;
    private GoogleMap mMap;
    private Polyline gpsTrack;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_see_activity, container, false);

        initializer(root);

        readData();

        setData();
        return root;
    }

    private void initializer(View root) {
        tv_distance = root.findViewById(R.id.tvTotalDistance_See);
        tv_time = root.findViewById(R.id.tvTotalTime_See);
        tv_calories = root.findViewById(R.id.tvTotalCalories_See);
        tv_average_pace = root.findViewById(R.id.tvAveragePace_See);
        tv_date = root.findViewById(R.id.tv_date);

        user = null;
        activity = null;

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.trackmapview_See);
        mapFragment.getMapAsync(this);
    }

    private void readData() {
        try {
            user = (User) InternalStorage.readObject(getContext(), "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            activity = (Activity) InternalStorage.readObject(getContext(), "Activity");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        tv_time.setText(activity.getTimeString());

        Conversion conversion = new Conversion();
        CaloriesBurned caloriesBurned = new CaloriesBurned(user.getWeight(), user.getUnits_weight(), user.getUnits_distance());

        double minutes = (activity.getTime() * 1.0f / 60000);
        double km = activity.getDistance() / 1000;

        if (!user.getUnits_distance().equals(getContext().getString(R.string.km))) {
            double mi = conversion.km_to_mi(km);

            tv_distance.setText(String.format("%.2f", mi) + " " + user.getUnits_distance());
            tv_average_pace.setText(caloriesBurned.calculateAveragePaceString(minutes, mi));
        } else {
            tv_distance.setText(String.format("%.2f", km) + " " + user.getUnits_distance());
            tv_average_pace.setText(caloriesBurned.calculateAveragePaceString(minutes, km));

        }

        tv_calories.setText(activity.getCalories() + "kcla");

        String[] day_of_week = getContext().getResources().getStringArray(R.array.name_day_of_week);

        tv_date.setText(day_of_week[activity.getDate().get(Calendar.DAY_OF_WEEK) - 1] + " - " + activity.getDate().get(Calendar.DAY_OF_MONTH) + "/" + (activity.getDate().get(Calendar.MONTH) + 1) + "/" + activity.getDate().get(Calendar.YEAR));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return;
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(10);
        gpsTrack = mMap.addPolyline(polylineOptions);

        List<LatLng> points = activity.getRoute();

        if (activity.getPointsofinterest().size() > 0) {
            for (MarkerOptions point : activity.getPointsofinterest()) {
                mMap.addMarker(point);
            }
        }

        mMap.addCircle(new CircleOptions()
                .center(points.get(0))
                .radius(10)
                .strokeColor(Color.GREEN)
                .fillColor(Color.GREEN));

        mMap.addCircle(new CircleOptions()
                .center(points.get((points.size() - 1)))
                .radius(10)
                .strokeColor(Color.BLUE)

                .fillColor(Color.BLUE));

        googleMap.addPolyline(polylineOptions.clickable(false).addAll(points));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 15));

    }

}
