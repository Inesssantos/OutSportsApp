package com.example.outsports.ui.group;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.outsports.R;
import com.example.outsports.data.User;

import java.util.List;

public class GroupAccessionCustomAdapter extends BaseAdapter {

    private final List<User> usersList;
    private final Context context;

    public GroupAccessionCustomAdapter(Context context, List<User> arrayList) {
        this.context = context;
        this.usersList = arrayList;
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
        return usersList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = usersList.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.fragment_group_accession_list_users, null);

            TextView tv_user_group_name = convertView.findViewById(R.id.tv_user_group_name_accession);
            final View mColorPreview = convertView.findViewById(R.id.vw_user_color_accession);

            tv_user_group_name.setText(user.getName());
            mColorPreview.setBackgroundColor(user.getColor());

        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return usersList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}