package com.example.outsports;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.outsports.data.CreateData;
import com.example.outsports.data.Group;
import com.example.outsports.data.InternalStorage;
import com.example.outsports.data.ListGroups;
import com.example.outsports.data.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        String primaryLocale = getResources().getConfiguration().getLocales().get(0).toString().toUpperCase();
        User user = null;

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Boolean firstTime = sharedPref.getBoolean("FirstTime", true);
        if (!firstTime) {
            createData();

            try {
                user = (User) InternalStorage.readObject(this, "User");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        String language;
        if (user != null) {
            language = user.getLanguage().toUpperCase();
        } else {
            language = "EN";
        }

        if (!primaryLocale.contains(language)) {
            switch (language) {
                case "EN":
                case "EN_US":
                    setLocale("en");
                    break;

                case "PT":
                case "PT_PT":
                    setLocale("pt");
                    break;

                case "IT":
                case "IT_IT":
                    setLocale("it");
                    break;
            }
        }
    }

    private void createData() {
        CreateData createData = new CreateData();
        /*try {
            InternalStorage.writeObject(this, "List Activities", createData.get_Activities());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        User user = null;
        try {
            user = (User) InternalStorage.readObject(this, "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (user != null) {
            Group group = createData.get_Group(user);

            ListGroups listGroups = new ListGroups();
            listGroups.addGroup(group);
            try {
                InternalStorage.writeObject(this, "List Groups", listGroups);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                InternalStorage.writeObject(this, "Group Simulation", group);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void setLocale(String language) {

        Configuration conf = new Configuration(this.getResources().getConfiguration());
        conf.locale = new Locale(language);
        this.getResources().updateConfiguration(conf, this.getResources().getDisplayMetrics());

        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        int id = navController.getCurrentDestination().getId();
        switch (id) {
            case R.id.statisticsMonthsFragment:
                navController.navigate(R.id.action_statisticsMonthsFragment_to_navigation_dashboard);
            break;

            case R.id.settingsFragment:
                navController.navigate(R.id.action_settingsFragment_to_navigation_notifications);
            break;

            case R.id.seeActivityFragment:

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                String previousFragment = sharedPref.getString("Previous Fragment", "Statistics Fragment");

                switch (previousFragment) {
                    case "Statistics Fragment":
                        navController.navigate(R.id.action_seeActivityFragment_to_navigation_dashboard);
                    break;

                    case "Statistics Months Fragment":
                        navController.navigate(R.id.action_seeActivityFragment_to_statisticsMonthsFragment);
                    break;
                }
            break;

            case R.id.accessionGroupFragment:
                navController.navigate(R.id.action_accessionGroupFragment_to_navigation_home);
                BottomNavigationView navView = this.findViewById(R.id.nav_view);
                navView.setVisibility(View.VISIBLE);
            break;

            case R.id.createGroupFragment:
                navController.navigate(R.id.action_createGroupFragment_to_navigation_home);
            break;
        }


        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        if(navController.getCurrentDestination().getId() != R.id.navigation_home){
            return;
        }

    }

}