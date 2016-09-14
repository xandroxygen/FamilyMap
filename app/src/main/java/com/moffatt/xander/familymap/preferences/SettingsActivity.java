package com.moffatt.xander.familymap.preferences;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.base.MainActivity;
import com.moffatt.xander.familymap.login.LoginFragment;
import com.moffatt.xander.familymap.login.LoginProxy;
import com.moffatt.xander.familymap.login.RequestController;
import com.moffatt.xander.familymap.login.RequestResponse;
import com.moffatt.xander.familymap.model.LineSetting;
import com.moffatt.xander.familymap.model.MapSetting;
import com.moffatt.xander.familymap.model.Setting;

import java.util.ArrayList;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity
    implements LoginProxy {

    LineSetting lifeLineSetting;
    LineSetting spouseLineSetting;
    LineSetting familyLineSetting;
    MapSetting mapSetting;

    Spinner sLifeLine;
    Spinner sSpouseLine;
    Spinner sFamilyLine;
    Spinner sMapType;

    Switch tLifeLine;
    Switch tSpouseLine;
    Switch tFamilyLine;

    LinearLayout resync;
    LinearLayout logout;

    RequestController resyncController;
    LoginFragment.REQUEST_TYPE requestType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("FamilyMap - Settings");

        setContentView(R.layout.settings_list);

        ArrayList<Setting> settings = FamilyReunion.getInstance().getSettings();
        lifeLineSetting = (LineSetting) settings.get(0);
        familyLineSetting = (LineSetting) settings.get(1);
        spouseLineSetting = (LineSetting) settings.get(2);
        mapSetting = (MapSetting) settings.get(3);

        resyncController = FamilyReunion.getInstance().getController();

        constructSpinners();

        setSpinnerListeners();

        setSwitchListeners();

        setOtherListeners();

    }

    /**
     * Sets spinner adapters and default values.
     */
    public void constructSpinners() {
        sLifeLine = (Spinner)findViewById(R.id.life_spinner);
        sSpouseLine = (Spinner) findViewById(R.id.spouse_spinner);
        sFamilyLine = (Spinner) findViewById(R.id.family_spinner);
        sMapType = (Spinner) findViewById(R.id.map_spinner);

        ArrayAdapter<CharSequence> lineAdapter = ArrayAdapter.createFromResource(this,
                R.array.lineColors, android.R.layout.simple_spinner_item);
        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> mapAdapter = ArrayAdapter.createFromResource(this,
                R.array.mapTypes, android.R.layout.simple_spinner_item);
        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        sLifeLine.setAdapter(lineAdapter);
        sSpouseLine.setAdapter(lineAdapter);
        sFamilyLine.setAdapter(lineAdapter);
        sMapType.setAdapter(mapAdapter);

        setDefaultSpinnerValues(sLifeLine, lifeLineSetting, lineAdapter);
        setDefaultSpinnerValues(sSpouseLine, spouseLineSetting, lineAdapter);
        setDefaultSpinnerValues(sFamilyLine, familyLineSetting, lineAdapter);
        setDefaultMapValue(sMapType, mapSetting, mapAdapter);


    }

    /**
     * Helper function: sets default value for line spinner,
     * using line setting value stored in model.
     * @param spinner to set
     * @param setting model setting
     * @param adapter array adapter
     */
    public void setDefaultSpinnerValues(Spinner spinner, LineSetting setting, ArrayAdapter<CharSequence> adapter) {
        switch (setting.getColor()) {
            case Color.RED:
                spinner.setSelection(adapter.getPosition("Red"));
                break;
            case Color.BLUE:
                spinner.setSelection(adapter.getPosition("Blue"));
                break;
            case Color.GREEN:
                spinner.setSelection(adapter.getPosition("Green"));
                break;
        }
    }

    /**
     * Helper function: sets default value for map spinner,
     * using map setting value stored in model.
     * @param map spinner
     * @param setting model setting
     * @param adapter array adapter
     */
    public void setDefaultMapValue(Spinner map, MapSetting setting, ArrayAdapter<CharSequence> adapter) {
        switch (setting.getType()) {
            case NORMAL:
                map.setSelection(adapter.getPosition("Normal"));
                break;
            case SATELLITE:
                map.setSelection(adapter.getPosition("Satellite"));
                break;
            case HYBRID:
                map.setSelection(adapter.getPosition("Hybrid"));
                break;
            case TERRAIN:
                map.setSelection(adapter.getPosition("Terrain"));
                break;
            default:
                map.setSelection(adapter.getPosition("Normal"));
                break;
        }
    }

    /**
     * Sets listeners for all spinners,
     * and updates proper setting when called.
     */
    public void setSpinnerListeners() {

        sLifeLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String color = (String) parent.getItemAtPosition(position);
                switch (color) {
                    case "Red":
                        lifeLineSetting.setColor(Color.RED);
                        break;
                    case "Blue":
                        lifeLineSetting.setColor(Color.BLUE);
                        break;
                    default:
                        lifeLineSetting.setColor(Color.GREEN);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do something?
            }
        });

        sSpouseLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String color = (String) parent.getItemAtPosition(position);
                switch (color) {
                    case "Green":
                        spouseLineSetting.setColor(Color.GREEN);
                        break;
                    case "Blue":
                        spouseLineSetting.setColor(Color.BLUE);
                        break;
                    default:
                        spouseLineSetting.setColor(Color.RED);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sFamilyLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String color = (String) parent.getItemAtPosition(position);
                switch (color) {
                    case "Red":
                        familyLineSetting.setColor(Color.RED);
                        break;
                    case "Green":
                        familyLineSetting.setColor(Color.GREEN);
                        break;
                    default:
                        familyLineSetting.setColor(Color.BLUE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) parent.getItemAtPosition(position);
                switch (type) {
                    case "Normal":
                        mapSetting.setType(Setting.MAP_TYPE.NORMAL);
                        break;
                    case "Satellite":
                        mapSetting.setType(Setting.MAP_TYPE.SATELLITE);
                        break;
                    case "Hybrid":
                        mapSetting.setType(Setting.MAP_TYPE.HYBRID);
                        break;
                    case "Terrain":
                        mapSetting.setType(Setting.MAP_TYPE.TERRAIN);
                        break;
                    default:
                        mapSetting.setType(Setting.MAP_TYPE.NORMAL);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Sets listeners for all switches,
     * and updates proper setting when called.
     */
    public void setSwitchListeners() {
        tLifeLine = (Switch)  findViewById(R.id.life_switch);
        tSpouseLine = (Switch)findViewById(R.id.spouse_switch);
        tFamilyLine = (Switch) findViewById(R.id.family_switch);

        tLifeLine.setChecked(lifeLineSetting.isDrawn());
        tSpouseLine.setChecked(spouseLineSetting.isDrawn());
        tFamilyLine.setChecked(familyLineSetting.isDrawn());

        tLifeLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lifeLineSetting.setDrawn(isChecked);
            }
        });

        tSpouseLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spouseLineSetting.setDrawn(isChecked);
            }
        });

        tFamilyLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                familyLineSetting.setDrawn(isChecked);
            }
        });
    }

    /**
     * Sets listeners for resync/logout,
     * and calls proper function.
     */
    public void setOtherListeners() {
        resync = (LinearLayout) findViewById(R.id.settings_resync);

        resync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resyncFamilyData();
            }
        });

        logout = (LinearLayout) findViewById(R.id.settings_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    /**
     * Called by resync button - refreshes family data,
     * but keeps user data/settings intact.
     * Makes Async requests.
     */
    public void resyncFamilyData() {

        FamilyReunion.getInstance().clear(false); // keep user login data

        FamilyReunion.getInstance().setController(resyncController);

        requestType = LoginFragment.REQUEST_TYPE.PEOPLE;

        resyncController.getAllPeople(FamilyReunion.getInstance().getCurrentUser().getAuthToken(), this);
    }

    /**
     * Called when logout button pressed.
     * Sets logged in flag to false, clears back stack,
     * and returns to Main.
     */
    public void logout() {
        MainActivity.isLoggedIn = false;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Overrides LoginProxy function to respond to Async requests for resyncing data.
     * Responds to event and person request and re-initializes data.
     * Keeps user and settings data and refreshes event and person data.
     * @param response
     */
    @Override
    public void onRequestResponse(RequestResponse response) {
        switch (requestType) {
            case PEOPLE:

                if (response.hasError()) {
                    showCenteredToast("Resync failed.");
                }
                else {
                    Object responseData = response.getData();

                    resyncController.populateModelWithPersons((String) responseData);

                    resyncController.getAllEvents(FamilyReunion.getInstance().getCurrentUser().getAuthToken(), this);

                    requestType = LoginFragment.REQUEST_TYPE.EVENTS;
                }
                break;
            case EVENTS:

                if (response.hasError()) {
                    showCenteredToast("Resync failed.");
                }
                else {
                    Object responseData = response.getData();

                    resyncController.populateModelWithEvents((String) responseData);

                    // initialize the rest of the model
                    resyncController.mapPersonsToEvents();

                    resyncController.mapChildrenToParents();

                    // keep settings instead of constructing new ones
                    FamilyReunion.getInstance().getSettings().add(lifeLineSetting);
                    FamilyReunion.getInstance().getSettings().add(familyLineSetting);
                    FamilyReunion.getInstance().getSettings().add(spouseLineSetting);
                    FamilyReunion.getInstance().getSettings().add(mapSetting);

                    resyncController.constructFilterEventList();

                    resyncController.populateFiltersWithEvents();

                    showCenteredToast("Data successfully resynced.");

                    this.finish();
                }
                break;
        }
    }

    private void showCenteredToast(String toastText) {
        Toast toast = Toast.makeText(this, toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
