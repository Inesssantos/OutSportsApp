package com.example.outsports.ui.group;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.outsports.R;
import com.example.outsports.data.User;

import java.util.List;

import top.defaults.colorpicker.ColorPickerPopup;

public class GroupCustomAdapter extends BaseAdapter {

    private final List<User> usersList;
    private final Context context;
    private final DeleteGroupUser mCallback;

    public GroupCustomAdapter(Context context, List<User> arrayList, DeleteGroupUser callback) {
        this.context = context;
        this.usersList = arrayList;
        mCallback = callback;
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
            convertView = layoutInflater.inflate(R.layout.fragment_group_list_users, null);

            TextView tv_user_group_name = convertView.findViewById(R.id.tv_user_group_name);
            final View mColorPreview = convertView.findViewById(R.id.preview_selected_color);
            ImageView imv_colorpick_user = convertView.findViewById(R.id.imv_colorpick_user);
            ImageView imv_remove_user = convertView.findViewById(R.id.imv_remove_user);

            if (position == 0) {
                imv_remove_user.setEnabled(false);
            }

            tv_user_group_name.setText(user.getName());
            mColorPreview.setBackgroundColor(user.getColor());

            imv_colorpick_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new ColorPickerPopup.Builder(context).initialColor(
                            Color.RED) // set initial color
                            // of the color
                            // picker dialog
                            .enableBrightness(
                                    true) // enable color brightness
                            // slider or not
                            .enableAlpha(
                                    true) // enable color alpha
                            // changer on slider or
                            // not
                            .okTitle(
                                    "Choose") // this is top right
                            // Choose button
                            .cancelTitle(
                                    "Cancel") // this is top left
                            // Cancel button which
                            // closes the
                            .showIndicator(
                                    true) // this is the small box
                            // which shows the chosen
                            // color by user at the
                            // bottom of the cancel
                            // button
                            .showValue(true) // this is the value which
                            // shows the selected
                            // color hex code
                            // the above all values can be made
                            // false to disable them on the
                            // color picker dialog.
                            .build()
                            .show(
                                    v,
                                    new ColorPickerPopup.ColorPickerObserver() {
                                        @Override
                                        public void
                                        onColorPicked(int color) {

                                            boolean allow = true;
                                            for (User value : usersList) {
                                                if (value.getColor() == color) {
                                                    allow = false;
                                                }
                                            }
                                            if (allow) {
                                                user.setColor(color);
                                                mColorPreview.setBackgroundColor(color);
                                                float[] hsv = new float[3];
                                                Color.colorToHSV(color, hsv);
                                                usersList.set(position, user);
                                            } else {
                                                Toast.makeText(context, context.getString(R.string.group_user_color_error_mensage), Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    }
                            );
                }
            });

            imv_remove_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, user.getName() + " " + context.getString(R.string.group_delete_user_mensage), Toast.LENGTH_LONG).show();
                    mCallback.DeleteGroupUser(user.getID());
                }
            });
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

    public List<User> getUsers() {
        return usersList;
    }

    public interface DeleteGroupUser {
        void DeleteGroupUser(int position);
    }
}