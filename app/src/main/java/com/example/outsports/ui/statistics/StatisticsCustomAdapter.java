package com.example.outsports.ui.statistics;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.outsports.R;
import com.example.outsports.data.Activity;
import com.example.outsports.data.Conversion;
import com.example.outsports.data.User;

import java.util.Calendar;
import java.util.List;


class StatisticsCustomAdapter implements ListAdapter {

    List<Activity> arrayList;
    User user;
    Context context;

    public StatisticsCustomAdapter(Context context, List<Activity> arrayList, User user) {
        this.context = context;
        this.arrayList = arrayList;
        this.user = user;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity subjectData = arrayList.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.fragment_statistics_months_list_item, null);

            TextView tv_distance = convertView.findViewById(R.id.tv_distance);
            TextView tv_time = convertView.findViewById(R.id.tv_time);
            TextView tv_date = convertView.findViewById(R.id.tv_date);

            if (!user.getUnits_distance().equals(context.getString(R.string.km))) {
                Conversion conversion = new Conversion();
                double mi = conversion.km_to_mi((subjectData.getDistance() / 1000));
                tv_distance.setText(String.format("%.2f", mi) + " " + user.getUnits_distance());
            } else {
                tv_distance.setText(String.format("%.2f", (subjectData.getDistance() / 1000)) + " " + user.getUnits_distance());
            }
            tv_time.setText(subjectData.getTimeString());

            String[] day_of_week = context.getResources().getStringArray(R.array.name_day_of_week);

            tv_date.setText(day_of_week[subjectData.getDate().get(Calendar.DAY_OF_WEEK) - 1] + " - " + subjectData.getDate().get(Calendar.DAY_OF_MONTH) + "/" + (subjectData.getDate().get(Calendar.MONTH) + 1) + "/" + subjectData.getDate().get(Calendar.YEAR));
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}