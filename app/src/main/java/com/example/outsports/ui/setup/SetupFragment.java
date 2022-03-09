package com.example.outsports.ui.setup;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.outsports.R;
import com.example.outsports.data.Conversion;
import com.example.outsports.data.InternalStorage;
import com.example.outsports.data.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class SetupFragment extends Fragment {

    private EditText etName, etWeight, etHeight, etEmail, etBirthday;
    private TextView tvContinue;

    private List<String> list_weight_units, list_height_units;
    private Spinner sp_weight, sp_height;
    private String weight_units, height_units;
    private Calendar birthday;
    private Conversion conversion;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_setup, container, false);

        initializer(root);

        return root;
    }

    private void initializer(final View root) {
        conversion = new Conversion();

        etName = root.findViewById(R.id.etName_setup);
        etWeight = root.findViewById(R.id.etWeight_setup);
        etHeight = root.findViewById(R.id.etHeight_setup);
        etEmail = root.findViewById(R.id.etEmail_setup);

        final DatePickerDialog[] picker = new DatePickerDialog[1];
        etBirthday = root.findViewById(R.id.etBirthday_setup);
        etBirthday.setInputType(InputType.TYPE_NULL);
        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker[0] = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar currentdate = Calendar.getInstance();
                                Calendar birthdaydate = Calendar.getInstance();
                                birthdaydate.set(year, monthOfYear, dayOfMonth);

                                int age = currentdate.get(Calendar.YEAR) - birthdaydate.get(Calendar.YEAR);
                                if (currentdate.get(Calendar.DAY_OF_YEAR) < birthdaydate.get(Calendar.DAY_OF_YEAR)) {
                                    age--;
                                }

                                if (age < 5) {
                                    Toast.makeText(getContext(), getString(R.string.birthday_error), Toast.LENGTH_LONG).show();
                                    etBirthday.setText("");
                                } else {
                                    etBirthday.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    birthday = birthdaydate;
                                }
                            }
                        }, year, month, day);
                picker[0].show();
            }
        });

        tvContinue = root.findViewById(R.id.tvContinue_setup);

        list_weight_units = new ArrayList<>();
        list_height_units = new ArrayList<>();

        list_weight_units.add(getString(R.string.kg));
        list_weight_units.add(getString(R.string.lb));

        list_height_units.add(getString(R.string.cm));
        list_height_units.add(getString(R.string.ft));

        sp_weight = root.findViewById(R.id.sp_weight);
        sp_height = root.findViewById(R.id.sp_height);

        ArrayAdapter<String> dataAdapter_weight = new ArrayAdapter<>(getContext(), R.layout.my_spinner, list_weight_units);
        dataAdapter_weight.setDropDownViewResource(R.layout.my_spinner);
        sp_weight.setAdapter(dataAdapter_weight);
        sp_weight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "Your choose :" + weight_units.get(position),Toast.LENGTH_SHORT).show();
                weight_units = list_weight_units.get(position);

                if (!etWeight.getText().toString().isEmpty()) {

                    float value = 0;

                    if (getString(R.string.kg) == (weight_units)) {
                        value = conversion.lb_to_kg(Float.valueOf(etWeight.getText().toString()));
                    } else {
                        value = conversion.kg_to_lb(Float.valueOf(etWeight.getText().toString()));
                    }

                    etWeight.setText(String.valueOf(value));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> dataAdapter_height = new ArrayAdapter<>(getContext(), R.layout.my_spinner, list_height_units);
        dataAdapter_height.setDropDownViewResource(R.layout.my_spinner);
        sp_height.setAdapter(dataAdapter_height);
        sp_height.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "Your choose :" + height_units.get(position),Toast.LENGTH_SHORT).show();
                height_units = list_height_units.get(position);

                if (!etHeight.getText().toString().isEmpty()) {

                    float value = 0;

                    if (getString(R.string.cm) == (height_units)) {
                        value = conversion.ft_to_cm(Float.valueOf(etHeight.getText().toString()));
                    } else {
                        value = conversion.cm_to_ft(Float.valueOf(etHeight.getText().toString()));
                    }

                    etHeight.setText(String.valueOf(value));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = applyChanges();
                if (success) {
                    Snackbar.make(requireView(), getString(R.string.save_sucess), Snackbar.LENGTH_SHORT).show();
                    NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.navigation_home, true).build();
                    navController.navigate(R.id.action_setupFragment_to_navigation_home, null, navOptions);
                    BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                    navView.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(requireView(), getString(R.string.fields_error), Snackbar.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean applyChanges() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (etName.getText().toString().isEmpty() || etWeight.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() || etBirthday.getText().toString().isEmpty()) {
            return false;
        } else {
            try {
                if (!etEmail.getText().toString().trim().matches(emailPattern)) {
                    return false;
                }

                saveData(etName.getText().toString(), Float.valueOf(etWeight.getText().toString()), Float.valueOf(etHeight.getText().toString()), etEmail.getText().toString());
                return true;
            } catch (Exception e) {
                Log.d("Erro:", e.toString());
                return false;
            }
        }
    }

    private void saveData(String name, float weight, float height, String email) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("FirstTime", false);
        /*editor.putString("Name", name);

        if (getString(R.string.cm) != (height_units)) {
            height = conversion.ft_to_cm(height);
        };

        if (getString(R.string.kg) != (weight_units)) {
            weight = conversion.lb_to_kg(weight);
        };

        editor.putFloat("Weight", weight);
        editor.putFloat("Height", height);

        editor.putString("Units_Height", height_units);
        editor.putString("Units_Weight", weight_units);*/
        editor.commit();

        try {
            InternalStorage.writeObject(getContext(), "User", new User(name, email, birthday, weight, height, "en", height_units, weight_units, "km"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
