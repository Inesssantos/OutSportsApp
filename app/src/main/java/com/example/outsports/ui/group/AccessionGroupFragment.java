package com.example.outsports.ui.group;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.outsports.R;
import com.example.outsports.data.Group;
import com.example.outsports.data.InternalStorage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class AccessionGroupFragment extends Fragment {

    private Button leave_group;
    private TextView etGroupName, etGroupDistance, etGroupUnitsDistance;
    private ListView lv_group_users;
    private GroupAccessionCustomAdapter groupCustomAdapter;
    private Group group;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_group_accession, container, false);

        loadData();

        initializer(root);

        listerns();

        return root;
    }

    private void loadData() {
        try {
            group = (Group) InternalStorage.readObject(getContext(), "Group");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initializer(View root) {
        leave_group = root.findViewById(R.id.bt_leave_group);
        etGroupName = root.findViewById(R.id.tv_group_name_accession);
        etGroupDistance = root.findViewById(R.id.tv_group_distance_accession);
        etGroupUnitsDistance = root.findViewById(R.id.tv_group_distance_units_accession);
        lv_group_users = root.findViewById(R.id.lv_group_users_accession);

        etGroupName.setText(group.getName());
        etGroupDistance.setText(String.valueOf(group.getDistance()));
        etGroupUnitsDistance.setText(group.getDistanceUnits());

        groupCustomAdapter = new GroupAccessionCustomAdapter(getContext(), group.getGroupUsers());
        lv_group_users.setAdapter(groupCustomAdapter);

    }

    private void listerns() {
        leave_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("Activity Group", false);

                final NavController navController = Navigation.findNavController(getView());
                navController.navigate(R.id.action_accessionGroupFragment_to_navigation_home);
                BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                navView.setVisibility(View.VISIBLE);
            }
        });
    }
}
