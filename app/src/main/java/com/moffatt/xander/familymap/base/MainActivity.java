package com.moffatt.xander.familymap.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.login.LoginFragment;
import com.moffatt.xander.familymap.map.MapFragment;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.person.PersonActivity;
import com.moffatt.xander.familymap.person.SearchActivity;
import com.moffatt.xander.familymap.preferences.FilterActivity;
import com.moffatt.xander.familymap.preferences.SettingsActivity;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnSuccessfulLoginListener,
        MapFragment.OnMapChangeActivityListener {

    LoginFragment loginFragment;
    MapFragment mapFragment;
    public ProgressDialog loadingDialog;
    Menu actionBarMenu;
    public static boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        loginFragment = new LoginFragment();
        mapFragment = new MapFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, loginFragment)
                .commit();

        if (isLoggedIn) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mapFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        actionBarMenu = menu;
        getMenuInflater().inflate(R.menu.map_frag_options, actionBarMenu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("isLoggedIn", isLoggedIn);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        isLoggedIn = savedInstanceState.getBoolean("isLoggedIn");

        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Called when user presses login button.
     * Displays dialog and starts login process.
     * @param v
     */
    public void onUserLogin(View v) {
        loadingDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Syncing data...");
        loadingDialog.show();

        isLoggedIn = false;
        loginFragment.onUserLogin();
    }

    @Override
    public ProgressDialog getLoadingDialog() {
        return loadingDialog;
    }

    /**
     * Called after login and data sync.
     * Switches from Login to Map Fragment.
     */
    @Override
    public void onSuccessfulLogin() {
        isLoggedIn = true;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Called when info card is clicked in Map Fragment.
     * Switches from Main Activity to Person Activity.
     */
    @Override
    public void onCardClick(String eventID) {
        Event event = FamilyReunion.getInstance().getEventIDtoEvent().get(eventID);

        Intent intent = new Intent(this, PersonActivity.class);
        intent.putExtra("personID", event.getPersonID());
        startActivity(intent);
    }

    @Override
    public void onMapLoad() {}

    /**
     * Called when options menu is clicked in Map Fragment.
     * Opens corresponding Activity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.search_icon:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings_icon:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.filter_icon:
                intent = new Intent(this, FilterActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
