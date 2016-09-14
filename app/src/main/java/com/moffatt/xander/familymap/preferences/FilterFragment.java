package com.moffatt.xander.familymap.preferences;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.Gravity;
import android.widget.Toast;

import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.model.Filter;

/**
 * Preference fragment that displays Settings for the app.
 * This includes Line types and colors, Map type,
 * Resyncing data, and Logging out.
 */
public class FilterFragment extends PreferenceFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener {


    public FilterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setPreferenceScreen(createFilterPreferences());
    }

    public PreferenceScreen createFilterPreferences() {
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(getActivity());

        for (Filter filter : FamilyReunion.getInstance().getFilters()) {
            SwitchPreference toggle = new SwitchPreference(getActivity());

            String title = getTitle(filter.getDescription());
            toggle.setTitle(title);

            String summary = getSummary(title);
            toggle.setSummary(summary);

            toggle.setKey(filter.getDescription());
            toggle.setChecked(filter.isSelected());

            root.addPreference(toggle);
        }
        return root;
    }

    public String getTitle(String desc) {
        String title = desc;
        if (title.equals("m")) {
            title = "Male Events";
        }
        else if (title.equals("f")) {
            title = "Female Events";
        }
        else if (title.equals("mother")) {
            title = "Mother's Side";
        }
        else if (title.equals("father")) {
            title = "Father's Side";
        }
        else {
            title = title.substring(0, 1).toUpperCase() + title.substring(1) + " Events";
        }
        return title;
    }

    public String getSummary(String title) {
        String summary = "Filter by " + title;
        if (title.equals("Mother's Side") ||
                title.equals("Father's Side")) {
            summary += " of Family";
        }
        return summary;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        for (Filter filter : FamilyReunion.getInstance().getFilters()) {
            if (filter.getDescription().equals(key)) {
                SwitchPreference toggle = (SwitchPreference) findPreference(key);
                if (toggle.isChecked()) {
                    filter.setSelected(true);
                    FamilyReunion.getInstance().enableFilter(filter);
                }
                else {
                    filter.setSelected(false);
                    FamilyReunion.getInstance().disableFilter(filter);

                }
            }
        }
    }

    public void showCenteredToast(String toastText) {
        Toast toast = Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
