package com.example.outsports.ui.group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.outsports.R;
import com.example.outsports.data.Group;
import com.example.outsports.data.InternalStorage;
import com.example.outsports.data.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.outsports.data.Constants.MAXIMUM_DISTANCE_KM;
import static com.example.outsports.data.Constants.MAXIMUM_DISTANCE_MI;
import static com.example.outsports.data.Constants.MINIMUM_DISTANCE_KM;
import static com.example.outsports.data.Constants.MINIMUM_DISTANCE_MI;

public class CreateGroupFragment extends Fragment implements GroupCustomAdapter.DeleteGroupUser {

    private EditText etGroupName, etGroupDistance;
    private Switch swGroupDistance;
    private TextView tvDistanceUnits;
    private Button bt_create_group, bt_start;
    private List<User> group_UserList;
    private ListView lv_group_users;
    private GroupCustomAdapter groupCustomAdapter;
    //
    private Button bt_simulate_users, bt_simulate_membership;
    private boolean set = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_group_create, container, false);

        initializer(root);

        listeners();

        return root;
    }


    private void initializer(View root) {
        etGroupName = root.findViewById(R.id.etGroupName);
        etGroupDistance = root.findViewById(R.id.etGroupDistance);
        swGroupDistance = root.findViewById(R.id.swGroupDistance);
        tvDistanceUnits = root.findViewById(R.id.tv_group_distance_units);
        bt_create_group = root.findViewById(R.id.bt_create_group);
        bt_start = root.findViewById(R.id.bt_start);
        lv_group_users = root.findViewById(R.id.lv_group_users);

        group_UserList = new ArrayList<>();
        User owner = null;
        try {
            owner = (User) InternalStorage.readObject(getContext(), "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (owner != null) {
            group_UserList.add(owner);
        }
        groupCustomAdapter = new GroupCustomAdapter(getContext(), group_UserList, CreateGroupFragment.this);
        lv_group_users.setAdapter(groupCustomAdapter);

        tvDistanceUnits.setText(owner.getUnits_distance());

        // Simulation //
        bt_simulate_users = root.findViewById(R.id.bt_simulate_users);
        bt_simulate_membership = root.findViewById(R.id.bt_simulate_membership);
        bt_simulate_membership.setEnabled(false);
    }

    private void listeners() {

        swGroupDistance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etGroupDistance.setEnabled(isChecked);
                swGroupDistance.setText((isChecked ? getString(R.string.group_distance_switch_on) : getString(R.string.group_distance_switch_off)));
            }
        });

        bt_create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (groupCustomAdapter != null) {
                    if (groupCustomAdapter.getCount() > 0) {
                        group_UserList = groupCustomAdapter.getUsers();
                        for (User value : group_UserList) {
                            if (value.getColor() == 0) {
                                Snackbar.make(requireView(), getString(R.string.group_color_error), Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }

                if (group_UserList.size() <= 1) {
                    Snackbar.make(requireView(), getString(R.string.group_min_user_error), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (etGroupName.getText().toString().isEmpty()) {
                    Snackbar.make(requireView(), getString(R.string.group_name_error), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (swGroupDistance.isChecked()) {
                    String value = etGroupDistance.getText().toString();
                    if (value.isEmpty()) {
                        Snackbar.make(requireView(), getString(R.string.group_distance_error), Snackbar.LENGTH_SHORT).show();
                        return;
                    } else {
                        float distance = Float.valueOf(etGroupDistance.getText().toString());

                        String comp = tvDistanceUnits.getText().toString();
                        if (tvDistanceUnits.getText().toString().equals(getString(R.string.km))) {
                            if (distance < MINIMUM_DISTANCE_KM || distance > MAXIMUM_DISTANCE_KM) {
                                etGroupDistance.setText("");
                                String mensage = String.format(getString(R.string.group_distance_invalid_error), MINIMUM_DISTANCE_KM, MAXIMUM_DISTANCE_KM);
                                Snackbar.make(requireView(), mensage, Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if (tvDistanceUnits.getText().toString().equals(getString(R.string.mi))) {
                            if (distance < MINIMUM_DISTANCE_MI || distance > MAXIMUM_DISTANCE_MI) {
                                etGroupDistance.setText("");
                                String mensage = String.format(getString(R.string.group_distance_invalid_error), MINIMUM_DISTANCE_MI, MAXIMUM_DISTANCE_MI);
                                Snackbar.make(requireView(), mensage, Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }

                Group newGroup = new Group();

                newGroup.setName(etGroupName.getText().toString());
                if (tvDistanceUnits.toString().equals(getString(R.string.km))) {
                    newGroup.setDistance((swGroupDistance.isChecked() ? Float.valueOf(etGroupDistance.getText().toString()) : 0));
                } else {
                    newGroup.setDistance((swGroupDistance.isChecked() ? Float.valueOf(etGroupDistance.getText().toString()) : 0));
                }

                newGroup.setDistanceUnits(tvDistanceUnits.toString());

                newGroup.setOwner(group_UserList.get(0));

                for (User value : group_UserList) {
                    newGroup.addUser(value);
                }

                /*ListGroups listGroups = new ListGroups();
                try {
                    listGroups = (ListGroups) InternalStorage.readObject(getContext(), "List Groups");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if(listGroups == null){
                    listGroups = new ListGroups();
                }
                listGroups.addGroup(newGroup);

                try {
                    InternalStorage.writeObject(getContext(), "List Groups", listGroups);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                try {
                    InternalStorage.writeObject(getContext(), "Group", newGroup);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getContext(), getString(R.string.mensage_group_create), Toast.LENGTH_LONG).show();

                bt_start.setEnabled(true);

            }
        });

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("Activity Group", true);
                editor.commit();

                final NavController navController = Navigation.findNavController(getView());
                navController.navigate(R.id.action_createGroupFragment_to_activityTrackingFragment);
                BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                navView.setVisibility(View.INVISIBLE);
            }
        });


        // Simulation //
        bt_simulate_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserList();
                bt_simulate_users.setEnabled(false);
                bt_simulate_membership.setEnabled(true);
            }
        });


        bt_simulate_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = "Test " + (groupCustomAdapter.getCount() + 1);
                String mensage = String.format(getString(R.string.group_add_user_mensage), name);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.join_group))
                        .setMessage(mensage)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (groupCustomAdapter.getCount() > 0) {
                                    group_UserList = groupCustomAdapter.getUsers();
                                }

                                Calendar birthday = Calendar.getInstance();
                                birthday.set(Calendar.YEAR, 2014);
                                group_UserList.add(new User(name, "a@a.a", birthday, 70f, 170f, "en", "cm", "kg", "km"));

                                groupCustomAdapter = new GroupCustomAdapter(getContext(), group_UserList, CreateGroupFragment.this);
                                lv_group_users.setAdapter(groupCustomAdapter);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), getString(R.string.group_acess_denied), Toast.LENGTH_LONG).show();
                            }
                        });

                //Creating dialog box
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void setUserList() {

        if (!set) {
            Group group = new Group();
            try {
                group = (Group) InternalStorage.readObject(getContext(), "Group Simulation");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            List<User> list_users = group.getGroupUsers();
            list_users.remove(0);
            group_UserList.addAll(list_users);

            groupCustomAdapter = new GroupCustomAdapter(getContext(), group_UserList, CreateGroupFragment.this);
            lv_group_users.setAdapter(groupCustomAdapter);

            set = true;
        }

    }

    @Override
    public void DeleteGroupUser(int position) {
        int index = 0;

        for (User value : group_UserList) {
            if (value.getID() == position) {
                group_UserList.remove(index);
                break;
            }
            index++;
        }

        groupCustomAdapter = new GroupCustomAdapter(getContext(), group_UserList, CreateGroupFragment.this);
        lv_group_users.setAdapter(groupCustomAdapter);
    }

    /* Function to update the user list if P2P work fine
    public void acessUser(final User newelement){
        String mensage = String.format(getString(R.string.group_add_user_mensage), newelement.getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.group_add_user_title))
                .setMessage(mensage)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(groupCustomAdapter.getCount() > 0){
                            group_UserList = groupCustomAdapter.getUsers();
                        }

                        newelement.setColor(0);
                        group_UserList.add(newelement);

                        groupCustomAdapter = new GroupCustomAdapter(getActivity(), group_UserList, CreateGroupFragment.this);
                        lv_group_users.setAdapter(groupCustomAdapter);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),getString(R.string.group_acess_denied), Toast.LENGTH_LONG).show();
                    }
                });

            //Creating dialog box
            AlertDialog dialog  = builder.create();
            dialog.show();
    }
     */
}
