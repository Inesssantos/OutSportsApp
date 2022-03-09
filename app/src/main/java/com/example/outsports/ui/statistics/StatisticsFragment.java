package com.example.outsports.ui.statistics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.outsports.R;
import com.example.outsports.data.Activity;
import com.example.outsports.data.Conversion;
import com.example.outsports.data.InternalStorage;
import com.example.outsports.data.ListActivities;
import com.example.outsports.data.User;
import com.example.outsports.ui.activity_tracking.CaloriesBurned;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private TextView tvRaceStatistics, tvError, tvMore;
    private BarChart chartRaceStatistics;
    private ListView lv_last_activities;

    private ListActivities listActivities;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        initializer(root);

        getLastActivitys(root);

        createChart();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_navigation_dashboard_to_statisticsMonthsFragment);
            }
        });

    }

    private void initializer(final View root) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Previous Fragment", "Statistics Fragment");
        editor.commit();

        listActivities = new ListActivities();
        try {
            listActivities = (ListActivities) InternalStorage.readObject(getContext(), "List Activities");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            user = (User) InternalStorage.readObject(getContext(), "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        chartRaceStatistics = root.findViewById(R.id.chartRaceStatistics);
        tvMore = root.findViewById(R.id.tvMore);
        tvRaceStatistics = root.findViewById(R.id.tvRaceStatistics);
        tvError = root.findViewById(R.id.tv_Error);
        lv_last_activities = root.findViewById(R.id.lv_last_activities);
    }

    private void getLastActivitys(final View root) {
        List<Activity> activities = null;

        if (listActivities != null) {
            activities = listActivities.getLastActivities();
        }


        if (activities == null || activities.size() == 0) {
            tvError.setVisibility(View.VISIBLE);
            tvMore.setVisibility(View.INVISIBLE);
            tvError.setText(getString(R.string.noactivity_recorded));
        } else {
            tvError.setVisibility(View.GONE);
            tvMore.setVisibility(View.VISIBLE);
            lv_last_activities.setVisibility(View.VISIBLE);

            StatisticsCustomAdapter statisticsCustomAdapter = new StatisticsCustomAdapter(getActivity(), activities, user);
            lv_last_activities.setAdapter(statisticsCustomAdapter);
            final List<Activity> finalActivities = activities;

            lv_last_activities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Activity selectActivity = finalActivities.get(position);

                    try {
                        InternalStorage.writeObject(getContext(), "Activity", selectActivity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    final NavController navController = Navigation.findNavController(root);
                    navController.navigate(R.id.action_navigation_dashboard_to_seeActivityFragment);
                }
            });

            lv_last_activities.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.delete_activity_title))
                            .setMessage(getString(R.string.delete_activity_message))
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity selectActivity = finalActivities.get(position);

                                    ListActivities listActivities = new ListActivities();
                                    try {
                                        listActivities = (ListActivities) InternalStorage.readObject(getContext(), "List Activities");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    listActivities.removeActivity(selectActivity.getId());

                                    try {
                                        InternalStorage.writeObject(getContext(), "List Activities", listActivities);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    final NavController navController = Navigation.findNavController(root);
                                    navController.navigate(R.id.action_navigation_dashboard_self);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    //Creating dialog box
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
        }
    }

    private void createChart() {
        if (listActivities != null) {
            Calendar rightNow = Calendar.getInstance();

            Conversion conversion = new Conversion();
            CaloriesBurned caloriesBurned = new CaloriesBurned(user.getWeight(), user.getUnits_weight(), user.getUnits_distance());

            double minutes = (listActivities.totalTimeYear(rightNow.get(Calendar.YEAR)) * 1.0f / 60000);
            double km = listActivities.totalDistanceYear(rightNow.get(Calendar.YEAR)) / 1000;

            String text = listActivities.totalActivitiesYear(rightNow.get(Calendar.YEAR)) + " " + getString(R.string.activities) + " - " + getString(R.string.average_pace) + " ";
            if (!user.getUnits_distance().equals(getContext().getString(R.string.km))) {
                text += caloriesBurned.calculateAveragePaceString(minutes, conversion.km_to_mi(km));

            } else {
                text += caloriesBurned.calculateAveragePaceString(minutes, km);
            }

            tvRaceStatistics.setText(text);

            ArrayList<BarEntry> BARENTRY;
            ArrayList<String> BarEntryLabels;
            BarDataSet Bardataset;
            BarData BARDATA;

            BARENTRY = new ArrayList<>();
            BarEntryLabels = new ArrayList<>();

            Resources res = getResources();
            String[] months = res.getStringArray(R.array.months);


            if (!user.getUnits_distance().equals(getContext().getString(R.string.km))) {
                for (int index = 0; index < months.length; index++) {
                    km = (listActivities.totalDistanceMonth(index) / 1000);
                    BARENTRY.add(new BarEntry(conversion.km_to_mi(km), index));
                }
            } else {
                for (int index = 0; index < months.length; index++) {
                    km = (listActivities.totalDistanceMonth(index) / 1000);
                    BARENTRY.add(new BarEntry((float) km, index));
                }
            }


            for (String value : months) {
                String m = String.valueOf(value.charAt(0));
                BarEntryLabels.add(m);
            }

            Bardataset = new BarDataSet(BARENTRY, "Total " + user.getUnits_distance() + " made monthly");

            BARDATA = new BarData(BarEntryLabels, Bardataset);

            Bardataset.setColors(Collections.singletonList(ContextCompat.getColor(getContext(), R.color.colorPrimary)));

            Bardataset.setValueTextColor(Color.BLACK);
            Bardataset.setValueTextSize(14f);

            chartRaceStatistics.setData(BARDATA);

            chartRaceStatistics.animateY(3000);

            XAxis xAxis = chartRaceStatistics.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(10);

            YAxis yAxis_left = chartRaceStatistics.getAxisLeft();
            yAxis_left.setDrawLabels(false);
            YAxis yAxis_right = chartRaceStatistics.getAxisRight();
            yAxis_right.setDrawLabels(false);
        }
    }

}