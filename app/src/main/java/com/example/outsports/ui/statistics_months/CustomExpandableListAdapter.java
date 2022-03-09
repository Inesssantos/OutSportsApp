package com.example.outsports.ui.statistics_months;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.outsports.R;
import com.example.outsports.data.Activity;
import com.example.outsports.data.Conversion;
import com.example.outsports.data.User;

import java.util.Calendar;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<ResumeMonth> expandableList;
    private final User user;

    public CustomExpandableListAdapter(Context context, List<ResumeMonth> listGroup, User user) {
        this.context = context;
        this.expandableList = listGroup;
        this.user = user;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableList.get(listPosition).getActivities(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.fragment_statistics_months_list_item, null);
        }

        Activity activity = expandableList.get(listPosition).getActivities(expandedListPosition);

        if (activity != null) {
            TextView tv_distance = convertView.findViewById(R.id.tv_distance);
            TextView tv_time = convertView.findViewById(R.id.tv_time);
            TextView tv_date = convertView.findViewById(R.id.tv_date);

            if (!user.getUnits_distance().equals(context.getString(R.string.km))) {
                Conversion conversion = new Conversion();
                double mi = conversion.km_to_mi((activity.getDistance() / 1000));
                tv_distance.setText(String.format("%.2f", mi) + " " + user.getUnits_distance());
            } else {
                tv_distance.setText(String.format("%.2f", (activity.getDistance() / 1000)) + " " + user.getUnits_distance());
            }
            tv_time.setText(activity.getTimeString());

            String[] day_of_week = context.getResources().getStringArray(R.array.name_day_of_week);

            tv_date.setText(day_of_week[activity.getDate().get(Calendar.DAY_OF_WEEK) - 1] + " - " + activity.getDate().get(Calendar.DAY_OF_MONTH) + "/" + (activity.getDate().get(Calendar.MONTH) + 1) + "/" + activity.getDate().get(Calendar.YEAR));
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        int size = this.expandableList.get(listPosition).getActivitiesSize();
        return size;
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableList.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableList.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.fragment_statistics_months_list_group, null);
        }

        ResumeMonth resumeMonth = expandableList.get(listPosition);

        TextView tvMonths = convertView.findViewById(R.id.tv_months);
        TextView tvTotalDistance = convertView.findViewById(R.id.tv_total_distance);
        TextView tvTotalActivities = convertView.findViewById(R.id.tv_total_activities);

        String month = resumeMonth.getMonth();
        float ditance = 0;
        if (!user.getUnits_distance().equals(context.getString(R.string.km))) {
            Conversion conversion = new Conversion();
            ditance = conversion.km_to_mi(resumeMonth.getTotal_distance());
        } else {
            ditance = resumeMonth.getTotal_distance();
        }
        String totaldistance = String.format("%.2f", ditance) + user.getUnits_distance();

        String totalactivities = String.valueOf(resumeMonth.getTotal_activities());

        tvMonths.setText(month);
        tvTotalDistance.setText(totaldistance);
        tvTotalActivities.setText(totalactivities);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
