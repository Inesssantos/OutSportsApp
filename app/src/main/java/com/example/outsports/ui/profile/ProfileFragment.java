package com.example.outsports.ui.profile;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.outsports.R;
import com.example.outsports.data.Constants;
import com.example.outsports.data.Conversion;
import com.example.outsports.data.InternalStorage;
import com.example.outsports.data.User;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

public class ProfileFragment extends Fragment {

    String units_height, units_weight;
    private EditText etName, etWeight, etHeight, etEmail, etBirthday;
    private Button btnCalc;
    private TextView tvResult, tvHeight, tvWeight;
    private TextView tvSettings;
    private Conversion conversion;
    private User user;
    private Calendar birthday;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_profile, container, false);

        initializer(root);

        loadData();

        return root;
    }

    private void initializer(final View root) {
        user = null;

        etName = root.findViewById(R.id.etName);
        etWeight = root.findViewById(R.id.etWeight);
        etHeight = root.findViewById(R.id.etHeight);
        etEmail = root.findViewById(R.id.etEmail);
        etBirthday = root.findViewById(R.id.etBirthday);

        btnCalc = root.findViewById(R.id.btnApplyChanges);

        tvResult = root.findViewById(R.id.tvRes);
        tvHeight = root.findViewById(R.id.tvHeight);
        tvWeight = root.findViewById(R.id.tvWeight);

        conversion = new Conversion();
        units_height = "";
        units_weight = "";

        final DatePickerDialog[] picker = new DatePickerDialog[1];
        etBirthday = root.findViewById(R.id.etBirthday);
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

        // Botão para calcular
        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = applyChanges();
                if (success) {
                    Snackbar.make(requireView(), getString(R.string.save_sucess), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(requireView(), getString(R.string.fields_error), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        tvSettings = root.findViewById(R.id.tv_settings);
        tvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(root);
                navController.navigate(R.id.action_navigation_notifications_to_settingsFragment);
            }
        });


    }

    private void loadData() {
        /*SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = sharedPref.getString("Name", "");
        Float weight = sharedPref.getFloat("Weight", 00f);
        Float height = sharedPref.getFloat("Height", 00f);

        units_height = sharedPref.getString("Units_Height", "");
        units_weight = sharedPref.getString("Units_Weight", "");*/


        try {
            user = (User) InternalStorage.readObject(getContext(), "User");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (user != null) {
            String name = user.getName();
            String email = user.getEmail();
            Float weight = user.getWeight();
            Float height = user.getHeight();
            birthday = user.getBirthday();
            units_height = user.getUnits_height();
            units_weight = user.getUnits_weight();

            if (!name.isEmpty() && weight != 00f && height != 00f && !email.isEmpty()) {
                etName.setText(name);

                etEmail.setText(email);

                if (!units_height.equals(getString(R.string.cm))) {
                    float converted_height = conversion.cm_to_ft(height);
                    etHeight.setText(String.valueOf(converted_height));
                } else {
                    etHeight.setText(String.valueOf(height));
                }

                if (!units_weight.equals(getString(R.string.kg))) {
                    float converted_weight = conversion.kg_to_lb(weight);
                    etWeight.setText(String.valueOf(converted_weight));
                } else {
                    etWeight.setText(String.valueOf(weight));
                }

                tvWeight.setText(units_weight);
                tvHeight.setText(units_height);

                etBirthday.setText(birthday.get(Calendar.DAY_OF_MONTH) + "/" + (birthday.get(Calendar.MONTH) + 1) + "/" + birthday.get(Calendar.YEAR));
                bmitoString(calcBMI(weight, (height / 100)));
            }

        }
    }

    private boolean applyChanges() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (etName.getText().toString().isEmpty() || etWeight.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() || etBirthday.getText().toString().isEmpty()) {
            return false;
        } else {
            try {
                float height = Float.valueOf(etHeight.getText().toString());
                float weight = Float.valueOf(etWeight.getText().toString());

                if (!units_height.equals(getString(R.string.cm))) {
                    height = conversion.ft_to_cm(height);
                }
                if (!units_weight.equals(getString(R.string.kg))) {
                    weight = conversion.lb_to_kg(weight);
                }

                if (!etEmail.getText().toString().trim().matches(emailPattern)) {
                    return false;
                }

                bmitoString(calcBMI(weight, (height / 100)));
                saveData(etName.getText().toString(), weight, height, etEmail.getText().toString());
                return true;
            } catch (Exception e) {
                Log.d("Erro:", e.toString());
                tvResult.setTextColor(ContextCompat.getColor(getContext(), R.color.errorColor));
                tvResult.setText(getText(R.string.err));
                return false;
            }
        }
    }

    private void saveData(String name, float weight, float height, String email) {
        /*SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Name", name);
        editor.putFloat("Weight", weight);
        editor.putFloat("Height", height);
        editor.commit();*/
        user.setName(name);
        user.setWeight(weight);
        user.setHeight(height);
        user.setEmail(email);
        user.setBirthday(birthday);

        try {
            InternalStorage.writeObject(getContext(), "User", user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double calcBMI(float peso, float altura) {
        return (peso / (altura * altura));
    }

    private void bmitoString(double calc) {
        DecimalFormat df = new DecimalFormat("#.00");
        String resp = "BMI: " + df.format(calc) + "\n";

        Resources res = getResources();
        String[] weight_status_names = res.getStringArray(R.array.weight_status_names);

        if (Constants.WEIGHT_STATUS_VALUES.size() != Constants.WEIGHT_STATUS_VALUES.size()) {
            tvResult.setTextColor(ContextCompat.getColor(getContext(), R.color.errorColor));
            tvResult.setText(getText(R.string.err));
        } else {
            int index = 0;

            for (Double[] values : Constants.WEIGHT_STATUS_VALUES.values()) {
                if (calc >= values[0].floatValue() && calc < values[1].floatValue()) {
                    resp += weight_status_names[index];
                    Integer color = Constants.WEIGHT_STATUS_COLORS.get(index);
                    tvResult.setTextColor(color);
                }
                index++;
            }

            tvResult.setText(resp);
        }
    }
}