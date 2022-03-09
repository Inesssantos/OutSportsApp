package com.example.outsports.ui.activity_tracking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.outsports.R;
import com.example.outsports.data.Activity;
import com.example.outsports.data.Conversion;
import com.example.outsports.data.Group;
import com.example.outsports.data.InternalStorage;
import com.example.outsports.data.ListActivities;
import com.example.outsports.data.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ServiceConnection;

import static android.content.ContentValues.TAG;

public class ActivityTrackingFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private static final int REQUEST_FINE_LOCATION = 44;
    private FloatingActionButton fab_parent_map, fab_satellite, fab_terrain, fab_normal;
    private TextView tv_satellite, tv_terrain, tv_normal;
    private Boolean isAllMapsFabsVisible;

    private FloatingActionButton fab_parent_group, fab_edit_group, fab_leave_group;
    private TextView tv_edit_group, tv_leave_group;
    private Boolean isAllGroupFabsVisible;

    private FloatingActionButton fab_parent_activity, fab_start, fab_pause, fab_stop, fab_points_interest;
    private TextView tv_start, tv_pause, tv_stop;
    private Boolean isAllActivityFabsVisible, isPause;

    private GoogleMap mMap;
    private Polyline gpsTrack;
    private GoogleApiClient googleApiClient;
    private LatLng atualKnownLatLng;


    private Chronometer chronometer;
    private TextView tvTotalDistance, tvTotalCalories, tvAveragePace;
    private long pauseOffSet;
    private double distance, last_distance;
    private String units_distance;
    private long time;

    private CaloriesBurned caloriesBurned;
    private Conversion conversion;

    private User user;
    private Group group;
    private boolean isgroup, isowner;
    private List<User> group_user_list;
    private List<Marker> markers_users_group;
    private List<Marker> markers_pointsofinterest;

    public static BitmapDescriptor getBitmapFromVector(@NonNull Context context, @DrawableRes int vectorResourceId,@ColorInt int tintColor) {

        Drawable vectorDrawable = ResourcesCompat.getDrawable(
                context.getResources(), vectorResourceId, null);
        if (vectorDrawable == null) {
            Log.e(TAG, "Requested vector resource was not found");
            return BitmapDescriptorFactory.defaultMarker();
        }
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, tintColor);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_activity_tracking, container, false);

        initialize(root);
        initializeGroup();
        initializeCaloriesBurned();
        initializeChronometer(root);

        floatingActionButtonsActivity(root);
        floatingActionButtonsMap(root);
        floatingActionButtonsGroup(root);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.trackmapview);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        return root;
    }

    private void initialize(View root) {
        tvTotalDistance = root.findViewById(R.id.tvTotalDistance);
        tvTotalCalories = root.findViewById(R.id.tvTotalCalories);
        tvAveragePace = root.findViewById(R.id.tvAveragePace);

        chronometer = root.findViewById(R.id.tvTotalTime);

        conversion = new Conversion();

        isPause = false;

        distance = 0;
        last_distance = 0;

        try {
            user = (User) InternalStorage.readObject(getContext(), "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        markers_pointsofinterest = new ArrayList<>();
    }

    private void initializeGroup() {

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        isgroup = sharedPref.getBoolean("Activity Group", false);

        if (isgroup) {
            try {
                group = (Group) InternalStorage.readObject(getContext(), "Group");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (group == null) {
                isgroup = false;
                return;
            }
            group_user_list = group.getGroupUsers();

            isowner = group.getOwner().getID() == user.getID();

            int index = 0;
            for (User value : group_user_list) {
                if (value.getID() == user.getID()) {
                    index = group_user_list.indexOf(value);
                    group_user_list.remove(index);
                    break;
                }
            }

            markers_users_group = new ArrayList<>();
        } else {
            group = null;
        }


    }

    private void initializeCaloriesBurned() {

        Float weight = user.getWeight();
        String units_weight = user.getUnits_weight();
        units_distance = user.getUnits_distance();

        caloriesBurned = new CaloriesBurned(weight, units_weight, units_distance);

        tvAveragePace.setText(caloriesBurned.calculateAveragePaceString(0, 0));
        tvTotalCalories.setText(caloriesBurned.calculateCaloriesBurnedString(0, 0));
        tvTotalDistance.setText("0.0" + units_distance);

    }

    private void initializeChronometer(View root) {
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String t = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
                chronometer.setText(t);

                double minutes = (time * 1.0f / 60000);
                if (minutes > 0.1 && distance > 1 && isPause) {
                    String averagepace;
                    String caloriesburned;

                    double km = (distance / 1000);
                    if (units_distance.equals(getContext().getString(R.string.km))) {
                        averagepace = caloriesBurned.calculateAveragePaceString(minutes, km);
                        caloriesburned = caloriesBurned.calculateCaloriesBurnedString(minutes, km);
                    } else {
                        averagepace = caloriesBurned.calculateAveragePaceString(minutes, conversion.km_to_mi(km));
                        caloriesburned = caloriesBurned.calculateCaloriesBurnedString(minutes, conversion.km_to_mi(km));
                    }
                    tvAveragePace.setText(averagepace);
                    tvTotalCalories.setText(caloriesburned);
                }

            }
        });
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setText("00:00:00");
        isPause = false;
    }

    private void floatingActionButtonsActivity(View root) {

        fab_points_interest = root.findViewById(R.id.fab_points_interest);
        fab_points_interest.setVisibility(View.GONE);

        fab_parent_activity = root.findViewById(R.id.fab_parent_activity);
        fab_start = root.findViewById(R.id.fab_start);
        fab_pause = root.findViewById(R.id.fab_pause);
        fab_stop = root.findViewById(R.id.fab_stop);

        tv_start = root.findViewById(R.id.tv_start);
        tv_pause = root.findViewById(R.id.tv_pause);
        tv_stop = root.findViewById(R.id.tv_stop);

        fab_start.setVisibility(View.GONE);
        fab_pause.setVisibility(View.GONE);
        fab_stop.setVisibility(View.GONE);

        tv_start.setVisibility(View.GONE);
        tv_pause.setVisibility(View.GONE);
        tv_stop.setVisibility(View.GONE);

        tv_start.bringToFront();
        tv_pause.bringToFront();
        tv_stop.bringToFront();

        isAllActivityFabsVisible = false;

        fab_parent_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllActivityFabsVisible) {
                    fab_parent_activity.setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24));
                    if (!isPause) {
                        fab_start.show();
                        fab_pause.show();
                        tv_start.setVisibility(View.VISIBLE);
                    } else {
                        fab_pause.show();
                        tv_pause.setVisibility(View.VISIBLE);
                    }
                    fab_stop.show();
                    tv_stop.setVisibility(View.VISIBLE);

                    isAllActivityFabsVisible = true;
                } else {
                    fab_parent_activity.setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_arrow_drop_up_24));
                    if (!isPause) {
                        fab_start.hide();
                        fab_pause.hide();
                        tv_start.setVisibility(View.GONE);
                    } else {
                        fab_pause.hide();
                        tv_pause.setVisibility(View.GONE);
                    }

                    fab_stop.hide();
                    tv_stop.setVisibility(View.GONE);

                    isAllActivityFabsVisible = false;
                }
            }
        });

        fab_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                fab_parent_activity.performClick();

                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffSet);
                chronometer.start();
                isPause = true;
            }
        });

        fab_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_parent_activity.performClick();

                chronometer.stop();
                pauseOffSet = SystemClock.elapsedRealtime() - chronometer.getBase();
                isPause = false;

            }
        });

        fab_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_parent_activity.performClick();

                if (time != 0 && distance != 0 && isPause) {
                    Toast.makeText(getContext(), (saveActivity() ? getString(R.string.save_activity_sucess) : getString(R.string.save_activity_error)), Toast.LENGTH_LONG).show();
                }

                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.setText("00:00:00");
                pauseOffSet = 0;

                isPause = false;
                distance = 0;
                time = 0;

                tvAveragePace.setText(caloriesBurned.calculateAveragePaceString(0, 0));
                tvTotalCalories.setText(caloriesBurned.calculateCaloriesBurnedString(0, 0));
                tvTotalDistance.setText("0.0" + units_distance);
                gpsTrack.remove();

                final NavController navController = Navigation.findNavController(getView());
                navController.navigate(R.id.action_activityTrackingFragment_to_navigation_home);
                BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                navView.setVisibility(View.VISIBLE);
            }
        });

        fab_points_interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (markers_pointsofinterest.size() > 0) {
                    List<String> pointsNames = new ArrayList<>();
                    for (Marker value : markers_pointsofinterest) {
                        pointsNames.add(value.getTitle());
                    }

                    //Create sequence of items
                    final CharSequence[] Groups = pointsNames.toArray(new String[pointsNames.size()]);

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    dialogBuilder.setTitle(getString(R.string.activity_delete_point_interest_title));

                    dialogBuilder.setItems(Groups, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, final int item) {
                            AlertDialog.Builder builder_admin = new AlertDialog.Builder(getContext());

                            builder_admin.setTitle(getString(R.string.activity_delete_point_interest_title))
                                    .setMessage(getString(R.string.activity_delete_point_interest_message))
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            markers_pointsofinterest.get(item).remove();
                                            markers_pointsofinterest.remove(item);

                                            if (markers_pointsofinterest.size() == 0) {
                                                fab_points_interest.setVisibility(View.GONE);
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
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
                }
            }
        });
    }

    private void floatingActionButtonsMap(View root) {
        fab_parent_map = root.findViewById(R.id.fab_parent_map_activity);
        fab_satellite = root.findViewById(R.id.fab_satellite_activity);
        fab_terrain = root.findViewById(R.id.fab_terrain_activity);
        fab_normal = root.findViewById(R.id.fab_normal_activity);

        tv_satellite = root.findViewById(R.id.tv_satellite_activity);
        tv_terrain = root.findViewById(R.id.tv_terrain_activity);
        tv_normal = root.findViewById(R.id.tv_normal_activity);

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
        fab_parent_group = root.findViewById(R.id.fab_parent_group_activity);
        fab_edit_group = root.findViewById(R.id.fab_edit_group_activity);
        fab_leave_group = root.findViewById(R.id.fab_leave_group_activity);

        tv_edit_group = root.findViewById(R.id.tv_edit_group_activity);
        tv_leave_group = root.findViewById(R.id.tv_leave_group_activity);

        fab_parent_group.setVisibility(View.GONE);

        fab_edit_group.setVisibility(View.GONE);
        fab_leave_group.setVisibility(View.GONE);

        tv_edit_group.setVisibility(View.GONE);
        tv_leave_group.setVisibility(View.GONE);

        tv_edit_group.bringToFront();
        tv_leave_group.bringToFront();

        isAllGroupFabsVisible = false;

        if (isgroup) {
            fab_parent_group.setVisibility(View.VISIBLE);

            fab_parent_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isAllGroupFabsVisible) {

                        if (isowner) {
                            fab_edit_group.show();
                            tv_edit_group.setVisibility(View.VISIBLE);
                        } else {
                            fab_leave_group.show();
                            tv_leave_group.setVisibility(View.VISIBLE);
                        }

                        isAllGroupFabsVisible = true;
                    } else {

                        if (isowner) {
                            fab_edit_group.hide();
                            tv_edit_group.setVisibility(View.GONE);
                        } else {
                            fab_leave_group.hide();
                            tv_leave_group.setVisibility(View.GONE);
                        }

                        isAllGroupFabsVisible = false;
                    }
                }
            });

            fab_edit_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (group_user_list.size() > 0) {
                        List<String> userNames = new ArrayList<>();
                        for (User value : group_user_list) {
                            userNames.add(value.getName());
                        }

                        //Create sequence of items
                        final CharSequence[] Groups = userNames.toArray(new String[userNames.size()]);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                        dialogBuilder.setTitle(getString(R.string.group_expel_user));

                        dialogBuilder.setItems(Groups, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, final int item) {
                                String mensage = String.format(getString(R.string.group_expel_user_mensage), group_user_list.get(item).getName());
                                AlertDialog.Builder builder_admin = new AlertDialog.Builder(getContext());

                                builder_admin.setTitle(getString(R.string.group_expel_user))
                                        .setMessage(mensage)
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                group_user_list.remove(item);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
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
                    }
                    fab_parent_group.performClick();
                }
            });

            fab_leave_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mensage = String.format(getString(R.string.group_add_user_mensage), user.getName());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.leave_group))
                            .setMessage(mensage)
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    final NavController navController = Navigation.findNavController(getView());
                                    navController.navigate(R.id.action_navigation_home_to_accessionGroupFragment);
                                    BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                                    navView.setVisibility(View.INVISIBLE);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    //Creating dialog box
                    AlertDialog dialog_admin = builder.create();
                    dialog_admin.show();

                    fab_parent_group.performClick();
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return;
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(10);
        gpsTrack = mMap.addPolyline(polylineOptions);

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);


        mMap.setMinZoomPreference(6.6f);
        mMap.setMaxZoomPreference(20.20f);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            LatLng atual = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atual, 15));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(final Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.fragment_marker_snippet, null);

                TextView tv_marker_name = v.findViewById(R.id.tv_marker_name);
                TextView tv_marker_distance = v.findViewById(R.id.tv_marker_distance);
                TextView tv_marker_distance_value = v.findViewById(R.id.tv_marker_distance_value);
                TextView tv_marker_latitude = v.findViewById(R.id.tv_marker_latitude);
                TextView tv_marker_longitude = v.findViewById(R.id.tv_marker_longitude);

                if (marker.getSnippet().isEmpty()) {
                    tv_marker_distance.setVisibility(View.GONE);
                    tv_marker_distance_value.setVisibility(View.GONE);
                } else {
                    tv_marker_distance.setVisibility(View.VISIBLE);
                    tv_marker_distance.setVisibility(View.VISIBLE);
                    final String[] text = marker.getSnippet().split(":", 2);
                    tv_marker_distance.setText(text[0]);
                }

                tv_marker_name.setText(marker.getTitle());
                tv_marker_latitude.setText(String.format("%.7f", marker.getPosition().latitude));
                tv_marker_longitude.setText(String.format("%.7f", marker.getPosition().longitude));

                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.activity_point_interest_name));

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);


                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name_place = input.getText().toString();
                        String mensage = "";
                        if (name_place.isEmpty()) {
                            dialog.cancel();
                        } else {
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(name_place).snippet("");
                            Marker marker = mMap.addMarker(markerOptions);
                            markers_pointsofinterest.add(marker);
                            fab_points_interest.setVisibility(View.VISIBLE);

                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        atualKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        updateTrack();
    }

    protected void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private void updateTrack() {
        List<LatLng> points = gpsTrack.getPoints();

        if (isPause) {
            if (points.size() > 1 && atualKnownLatLng != null) {
                distance += calculateDistance(points.get(points.size() - 1), atualKnownLatLng);

                if (units_distance.equals(getContext().getString(R.string.km))) {
                    tvTotalDistance.setText(String.format("%.2f", (distance / 1000)) + units_distance);
                } else {
                    tvTotalDistance.setText(String.format("%.2f", conversion.km_to_mi((float) (distance / 1000))) + units_distance);
                }
            }

            if (isgroup) {

                Double latitude = atualKnownLatLng.latitude, longitude = atualKnownLatLng.longitude;
                if (group_user_list.size() > 0) {
                    //Simulate data
                    for (User value : group_user_list) {
                        latitude = latitude - 0.0001;
                        longitude = longitude - 0.0001;
                        value.setLast_location(new LatLng(latitude, longitude));
                        group_user_list.set(group_user_list.indexOf(value), value);
                    }

                    if (markers_users_group.size() > 0) {
                        for (Marker value : markers_users_group) {
                            value.remove();
                        }
                    }
                    markers_users_group.clear();

                    for (User value : group_user_list) {
                        float[] hsv = new float[3];
                        Color.colorToHSV(value.getColor(), hsv);

                        String mensage = "";
                        if (units_distance.equals(getContext().getString(R.string.km))) {
                            mensage = String.format("%s:", (String.format("%.3f", (calculateDistance(value.getLast_location(), atualKnownLatLng) / 1000)) + units_distance));
                        } else {
                            mensage = String.format("%s:", (String.format("%.3f", conversion.km_to_mi((float) (calculateDistance(value.getLast_location(), atualKnownLatLng) / 1000))) + units_distance));
                        }

                        MarkerOptions markerOptions = new MarkerOptions().position(value.getLast_location()).title(value.getName()).snippet(mensage).icon(getBitmapFromVector(getContext(), R.drawable.ic_baseline_account_circle_24, value.getColor()));
                        Marker marker = mMap.addMarker(markerOptions);
                        markers_users_group.add(marker);
                        //Log.e("Marker",mensage);
                        //mMap.addMarker(new MarkerOptions().position(value.getLast_location()).title(value.getName()).snippet(mensage).icon(BitmapDescriptorFactory.defaultMarker(hsv[0])));
                    }

                    if (group.getDistance() > 0) {
                        boolean its_far = false;
                        for (User value : group_user_list) {
                            if (group.getDistanceUnits().equals(getContext().getString(R.string.km))) {
                                if ((calculateDistance(value.getLast_location(), atualKnownLatLng) / 1000) > group.getDistance()) {
                                    its_far = true;
                                }
                            } else {
                                if ((conversion.km_to_mi((float) (calculateDistance(value.getLast_location(), atualKnownLatLng) / 1000))) > group.getDistance()) {
                                    its_far = true;
                                }
                            }
                            if (its_far) {
                                String mensage = String.format(getString(R.string.group_distance_mensage), value.getName());
                                new AlertDialog.Builder(getContext())
                                        .setTitle(getString(R.string.group_distance))
                                        .setMessage(mensage)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        }).show();
                            }
                        }
                    }
                }
            }

            points.add(atualKnownLatLng);
            gpsTrack.setPoints(points);

            LatLng atual = new LatLng(atualKnownLatLng.latitude, atualKnownLatLng.longitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atual, 15));
        }
    }

    private double calculateDistance(LatLng lastKnownLatLng, LatLng atualKnownLatLng) {
        Location selected_location = new Location("locationA");
        selected_location.setLatitude(lastKnownLatLng.latitude);
        selected_location.setLongitude(lastKnownLatLng.longitude);

        Location near_locations = new Location("locationB");
        near_locations.setLatitude(atualKnownLatLng.latitude);
        near_locations.setLongitude(atualKnownLatLng.longitude);

        return selected_location.distanceTo(near_locations);
    }

    private boolean saveActivity() {

        if (distance > 10) {
            Activity newActivity = new Activity();

            newActivity.setTime(time);
            newActivity.setDistance((float) distance);
            double minutes = (time * 1.0f / 60000);
            newActivity.setCalories((int) caloriesBurned.calculateCaloriesBurned(minutes, (distance / 1000)));
            newActivity.setRoute(gpsTrack.getPoints());
            newActivity.setDate(Calendar.getInstance());
            newActivity.setPointsofinterest(markers_pointsofinterest);

            ListActivities listActivities = new ListActivities();
            try {
                listActivities = (ListActivities) InternalStorage.readObject(getContext(), "List Activities");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (listActivities == null) {
                listActivities = new ListActivities();
            }

            listActivities.addActivity(newActivity);

            try {
                InternalStorage.writeObject(getContext(), "List Activities", listActivities);
            } catch (IOException e) {
                e.printStackTrace();

            }

            return true;
        }
        return false;


    }


}