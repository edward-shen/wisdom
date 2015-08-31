package tk.easthigh.witsmobile;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import tk.easthigh.witsmobile.tools.CircularImageView;
import tk.easthigh.witsmobile.tools.DataManager;
import tk.easthigh.witsmobile.tools.SecurePreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Spinner spinner;
    private Context context;
    private View view;
    private Activity activity;
    private SecurePreferences sPrefs;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        activity = getActivity();
        context = activity.getApplicationContext();
        sPrefs = new DataManager().getSecurePrefs(context);

        setSpinner();
        setSwitch();

        return view;
    }

    private void setSpinner() {
        spinner  = (Spinner) view.findViewById(R.id.settings_color_spinner);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.settings_color, android.R.layout.simple_spinner_item);
        final SecurePreferences sPrefs = new DataManager().getSecurePrefs(activity.getApplicationContext());

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);


        if (sPrefs.getString("theme") != null) {
            Log.i("SettingsFragment", "theme is not null!");
            switch (sPrefs.getString("theme")) {
                case "east":
                    spinner.setSelection(0);
                    break;
                case "north":
                    spinner.setSelection(1);
                    break;
                case "south":
                    spinner.setSelection(2);
                    break;
            }
        } else
            Log.w("SettingsFragment", "theme is null!");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using

                String temp = "";
                if (sPrefs.getString("theme") != null)
                    temp = sPrefs.getString("theme");

                switch (pos) {
                    case 0:
                        sPrefs.put("theme", "east");
                        break;
                    case 1:
                        sPrefs.put("theme", "north");
                        break;
                    case 2:
                        sPrefs.put("theme", "south");
                        break;
                }


                if (!temp.equals(sPrefs.getString("theme"))){
                    sPrefs.put("themeSwitch", "true");
                    activity.recreate();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    public void setSwitch(){
        final Switch photoSwitch = (Switch) view.findViewById(R.id.settings_profile_photo_switch);

        if (sPrefs.getString("userLoggedIn") == null || sPrefs.getString("userLoggedIn").equals("false"))
            photoSwitch.setEnabled(false);

        final CircularImageView circle = (CircularImageView) activity.findViewById(R.id.profile_img);

        photoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    circle.setVisibility(View.VISIBLE);
                } else {
                    circle.setVisibility(View.INVISIBLE);
                }

            }
        });
    }
}
