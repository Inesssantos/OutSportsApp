package com.example.outsports.ui.settings;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.outsports.MainActivity;
import com.example.outsports.R;
import com.example.outsports.data.InternalStorage;
import com.example.outsports.data.User;

import java.io.IOException;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    RadioGroup rg_distance, rg_weight, rg_height, rg_language;
    RadioButton rb_Km, rb_Mi, rb_Kg, rb_Lb, rb_M, rb_Ft;
    RadioButton rb_English, rb_Portuguese, rb_Italian;

    Button bt_save_settings;

    String language;
    User user;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_settings, container, false);

        initializer(root);

        loadData();

        saveSettings();

        return root;
    }

    private void initializer(View root) {
        rg_distance = root.findViewById(R.id.rg_distance);
        rg_weight = root.findViewById(R.id.rg_weight);
        rg_height = root.findViewById(R.id.rg_height);
        rg_language = root.findViewById(R.id.rg_language);

        rb_Km = root.findViewById(R.id.rb_Km);
        rb_Mi = root.findViewById(R.id.rb_Mi);

        rb_Kg = root.findViewById(R.id.rb_Kg);
        rb_Lb = root.findViewById(R.id.rb_Lb);

        rb_M = root.findViewById(R.id.rb_Cm);
        rb_Ft = root.findViewById(R.id.rb_Ft);

        rb_English = root.findViewById(R.id.rb_English);
        rb_Portuguese = root.findViewById(R.id.rb_Portuguese);
        rb_Italian = root.findViewById(R.id.rb_Italian);

        bt_save_settings = root.findViewById(R.id.bt_save_settings);

        language = "";

        user = null;
    }

    private void loadData() {
        /*SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        language = sharedPref.getString("Language", "English");
        String units_height = sharedPref.getString("Units_Height", getString(R.string.cm));
        String units_weight = sharedPref.getString("Units_Weight", getString(R.string.kg));
        String units_distance = sharedPref.getString("Units_Distance", getString(R.string.km));*/

        try {
            user = (User) InternalStorage.readObject(getContext(), "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (user != null) {
            language = user.getLanguage();
            String units_height = user.getUnits_height();
            String units_weight = user.getUnits_weight();
            String units_distance = user.getUnits_distance();

            if (!language.isEmpty() && !units_height.isEmpty() && !units_weight.isEmpty() && !units_distance.isEmpty()) {
                switch (language) {
                    case "en":
                        rb_English.toggle();
                        break;

                    case "pt":
                        rb_Portuguese.toggle();
                        break;

                    case "it":
                        rb_Italian.toggle();
                        break;
                }

                switch (units_height) {
                    case "cm":
                        rb_M.toggle();
                        break;
                    case "ft":
                        rb_Ft.toggle();
                        break;
                }

                switch (units_weight) {
                    case "kg":
                        rb_Kg.toggle();
                        break;
                    case "lb":
                        rb_Lb.toggle();
                        break;
                }

                switch (units_distance) {
                    case "km":
                        rb_Km.toggle();
                        break;
                    case "mi":
                        rb_Mi.toggle();
                        break;
                }
            }
        }
    }

    private void saveSettings() {
        bt_save_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language_chosen, units_height, units_weight, units_distance;

                int select_distance = rg_distance.getCheckedRadioButtonId();
                int select_weight = rg_weight.getCheckedRadioButtonId();
                int select_height = rg_height.getCheckedRadioButtonId();
                int select_language = rg_language.getCheckedRadioButtonId();

                switch (select_language) {
                    case R.id.rb_English:
                        language_chosen = "en";
                        break;
                    case R.id.rb_Portuguese:
                        language_chosen = "pt";
                        break;
                    case R.id.rb_Italian:
                        language_chosen = "it";
                        break;
                    default:
                        language_chosen = "en";
                }

                switch (select_height) {
                    case R.id.rb_Cm:
                        units_height = "cm";
                        break;
                    case R.id.rb_Ft:
                        units_height = "ft";
                        break;
                    default:
                        units_height = "cm";
                }

                switch (select_weight) {
                    case R.id.rb_Kg:
                        units_weight = "kg";
                        break;
                    case R.id.rb_Lb:
                        units_weight = "lb";
                        break;
                    default:
                        units_weight = "kg";
                }

                switch (select_distance) {
                    case R.id.rb_Km:
                        units_distance = "km";
                        break;
                    case R.id.rb_Mi:
                        units_distance = "mi";
                        break;
                    default:
                        units_distance = "km";
                }

                saveData(language_chosen, units_height, units_weight, units_distance);
            }
        });
    }

    private void saveData(String language_chosen, String units_height, String units_weight, String units_distance) {

        /*SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("Language", language_chosen);
        editor.putString("Units_Height", units_height);
        editor.putString("Units_Weight", units_weight);
        editor.putString("Units_Distance", units_distance);

        editor.commit();*/

        user.setLanguage(language_chosen);
        user.setUnits_height(units_height);
        user.setUnits_weight(units_weight);
        user.setUnits_distance(units_distance);

        try {
            InternalStorage.writeObject(getContext(), "User", user);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!language.equals(language_chosen)) {
            setLocale(language_chosen);
        }
    }

    private void setLocale(String language) {

        Configuration conf = new Configuration(getContext().getResources().getConfiguration());
        conf.locale = new Locale(language);
        getContext().getResources().updateConfiguration(conf, getContext().getResources().getDisplayMetrics());

        Intent refresh = new Intent(getContext(), MainActivity.class);
        startActivity(refresh);
    }
}
