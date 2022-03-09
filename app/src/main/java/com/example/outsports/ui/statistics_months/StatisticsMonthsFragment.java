package com.example.outsports.ui.statistics_months;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.outsports.R;
import com.example.outsports.data.Activity;
import com.example.outsports.data.InternalStorage;
import com.example.outsports.data.ListActivities;
import com.example.outsports.data.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsMonthsFragment extends Fragment {

    private ExpandableListView expandableListView;
    private List<ResumeMonth> listGroup;
    private ListActivities listActivities;
    private CustomExpandableListAdapter customExpandableListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics_months, container, false);

        setHasOptionsMenu(true);

        initialize(root);

        return root;
    }

    private void initialize(final View root) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Previous Fragment", "Statistics Months Fragment");
        editor.commit();

        // Retrieve the list from internal storage
        try {
            listActivities = (ListActivities) InternalStorage.readObject(getContext(), "List Activities");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        User user = null;
        try {
            user = (User) InternalStorage.readObject(getContext(), "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        expandableListView = root.findViewById(R.id.expandablelistView);
        listGroup = new ArrayList<>();

        initializeExpandableList();

        CustomExpandableListAdapter expandableListAdapter = new CustomExpandableListAdapter(getContext(), listGroup, user);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Activity selectActivity = listGroup.get(groupPosition).getActivities(childPosition);

                try {
                    InternalStorage.writeObject(getContext(), "Activity", selectActivity);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final NavController navController = Navigation.findNavController(root);
                navController.navigate(R.id.action_statisticsMonthsFragment_to_seeActivityFragment);

                return false;
            }
        });

    }

    private void initializeExpandableList() {
        Calendar c = Calendar.getInstance();
        int cmonth = c.get(Calendar.MONTH) + 1;

        String[] months = getResources().getStringArray(R.array.months);


        for (int index = 0; index < cmonth; index++) {
            ResumeMonth resumeMonth = null;
            if (listActivities == null || listActivities.totalActivitiesMonth(index) == 0) {
                resumeMonth = new ResumeMonth(months[index], 0, 0, null);
            } else {
                resumeMonth = new ResumeMonth(months[index], listActivities.totalActivitiesMonth(index), (listActivities.totalDistanceMonth(index) / 1000), listActivities.getActivitiesMonth(index));
            }
            listGroup.add(resumeMonth);
        }

    }


}