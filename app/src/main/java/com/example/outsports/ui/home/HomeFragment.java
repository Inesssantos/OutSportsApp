package com.example.outsports.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.outsports.R;
import com.example.outsports.data.Group;
import com.example.outsports.data.InternalStorage;
import com.example.outsports.data.ListGroups;
import com.example.outsports.data.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_FINE_LOCATION = 44;
    FloatingActionButton fab_parent_map, fab_satellite, fab_terrain, fab_normal;
    TextView tv_satellite, tv_terrain, tv_normal;
    Boolean isAllMapsFabsVisible;
    FloatingActionButton fab_parent_group, fab_create_group, fab_join_group;
    TextView tv_create_group, tv_join_group;
    Boolean isAllGroupFabsVisible;
    FloatingActionButton fab_start;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        floatingActionButtonsMapStyle(root);
        floatingActionButtonsGroup(root);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        fab_start = root.findViewById(R.id.fab_start);
        fab_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("Activity Group", false);
                editor.commit();

                final NavController navController = Navigation.findNavController(root);
                navController.navigate(R.id.action_navigation_home_to_activityTrackingFragment);
                BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                navView.setVisibility(View.INVISIBLE);
            }
        });

        return root;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        mMap = googleMap;
        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng newlatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newlatLng, 18));
                }
            }
        });
    }


    private void floatingActionButtonsMapStyle(View root) {

        fab_parent_map = root.findViewById(R.id.fab_parent_map);
        fab_satellite = root.findViewById(R.id.fab_satellite);
        fab_terrain = root.findViewById(R.id.fab_terrain);
        fab_normal = root.findViewById(R.id.fab_normal);

        tv_satellite = root.findViewById(R.id.tv_satellite);
        tv_terrain = root.findViewById(R.id.tv_terrain);
        tv_normal = root.findViewById(R.id.tv_normal);

        fab_satellite.setVisibility(View.GONE);
        fab_terrain.setVisibility(View.GONE);
        fab_normal.setVisibility(View.GONE);

        tv_satellite.setVisibility(View.GONE);
        tv_terrain.setVisibility(View.GONE);
        tv_normal.setVisibility(View.GONE);

        tv_satellite.bringToFront();
        tv_terrain.bringToFront();
        tv_normal.bringToFront();

        isAllMapsFabsVisible = false;

        fab_parent_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllMapsFabsVisible) {
                    if (isAllGroupFabsVisible) {
                        fab_parent_group.performClick();
                    }

                    fab_satellite.show();
                    fab_terrain.show();
                    fab_normal.show();

                    tv_satellite.setVisibility(View.VISIBLE);
                    tv_terrain.setVisibility(View.VISIBLE);
                    tv_normal.setVisibility(View.VISIBLE);

                    isAllMapsFabsVisible = true;
                } else {

                    fab_satellite.hide();
                    fab_terrain.hide();
                    fab_normal.hide();

                    tv_satellite.setVisibility(View.GONE);
                    tv_terrain.setVisibility(View.GONE);
                    tv_normal.setVisibility(View.GONE);

                    isAllMapsFabsVisible = false;
                }
            }
        });

        fab_terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                fab_parent_map.performClick();
            }
        });

        fab_satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                fab_parent_map.performClick();
            }
        });

        fab_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                fab_parent_map.performClick();
            }
        });

    }

    private void floatingActionButtonsGroup(View root) {

        fab_parent_group = root.findViewById(R.id.fab_parent_group);
        fab_create_group = root.findViewById(R.id.fab_create_group);
        fab_join_group = root.findViewById(R.id.fab_join_group);

        tv_create_group = root.findViewById(R.id.tv_create_group);
        tv_join_group = root.findViewById(R.id.tv_join_group);

        fab_create_group.setVisibility(View.GONE);
        fab_join_group.setVisibility(View.GONE);

        tv_create_group.setVisibility(View.GONE);
        tv_join_group.setVisibility(View.GONE);

        tv_create_group.bringToFront();
        tv_join_group.bringToFront();

        isAllGroupFabsVisible = false;

        fab_parent_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllGroupFabsVisible) {

                    fab_create_group.show();
                    fab_join_group.show();

                    tv_create_group.setVisibility(View.VISIBLE);
                    tv_join_group.setVisibility(View.VISIBLE);

                    isAllGroupFabsVisible = true;
                } else {

                    fab_create_group.hide();
                    fab_join_group.hide();

                    tv_create_group.setVisibility(View.GONE);
                    tv_join_group.setVisibility(View.GONE);

                    isAllGroupFabsVisible = false;
                }
            }
        });

        fab_create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NavController navController = Navigation.findNavController(getView());
                navController.navigate(R.id.action_navigation_home_to_createGroupFragment);
                fab_parent_group.performClick();
            }
        });

        fab_join_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListGroups listGroups = new ListGroups();

                try {
                    listGroups = (ListGroups) InternalStorage.readObject(getContext(), "List Groups");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (listGroups.getSizeGroups() > 0) {
                    List<String> groupNmaes = new ArrayList<>();
                    for (Group value : listGroups.getListGroups()) {
                        groupNmaes.add(value.getName());
                    }

                    //Create sequence of items
                    final CharSequence[] Groups = groupNmaes.toArray(new String[groupNmaes.size()]);

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    dialogBuilder.setTitle(getString(R.string.group_choose));

                    final ListGroups finalListGroups = listGroups;
                    dialogBuilder.setItems(Groups, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, final int item) {
                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("Activity Group", true);

                            User user = null;
                            try {
                                user = (User) InternalStorage.readObject(getContext(), "User");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getContext(), "Send request to join the group via P2P", Toast.LENGTH_SHORT).show();

                            /// form that appears to the group administrator to allow or deny user access

                            Toast.makeText(getContext(), "form that appears to the group administrator to allow or deny user access", Toast.LENGTH_SHORT).show();

                            String mensage = String.format(getString(R.string.group_add_user_mensage), user.getName());
                            AlertDialog.Builder builder_admin = new AlertDialog.Builder(getContext());
                            final User finalUser = user;

                            Random rnd = new Random();
                            finalUser.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));

                            builder_admin.setTitle(getString(R.string.join_group))
                                    .setMessage(mensage)
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Group newGroup = finalListGroups.getGroup(item);
                                            newGroup.addUser(finalUser);

                                            try {
                                                InternalStorage.writeObject(getContext(), "Group", newGroup);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            final NavController navController = Navigation.findNavController(getView());
                                            navController.navigate(R.id.action_navigation_home_to_accessionGroupFragment);
                                            BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                                            navView.setVisibility(View.INVISIBLE);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getContext(), getString(R.string.group_acess_denied), Toast.LENGTH_LONG).show();
                                        }
                                    });

                            //Creating dialog box
                            AlertDialog dialog_admin = builder_admin.create();
                            dialog_admin.show();
                        }
                    });
                    //Create alert dialog object via builder
                    AlertDialog alertDialogObject = dialogBuilder.create();
                    //Show the dialog
                    alertDialogObject.show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.group_add_user_title))
                            .setMessage(getString(R.string.group_available))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                }

                fab_parent_group.performClick();
            }
        });

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        Boolean firstTime = sharedPref.getBoolean("FirstTime", false);
        User user = null;
        try {
            user = (User) InternalStorage.readObject(getContext(), "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (firstTime || user == null) {
            navController.navigate(R.id.action_navigation_home_to_setupFragment);
            BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
            navView.setVisibility(View.INVISIBLE);
        }
    }

}