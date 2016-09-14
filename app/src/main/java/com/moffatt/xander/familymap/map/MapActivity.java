package com.moffatt.xander.familymap.map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.base.MainActivity;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.person.PersonActivity;

/**
 * Contains a Map Fragment. Called by person/search activity.
 * Centers on selected eventID.
 * Created by Xander on 8/2/2016.
 */
public class MapActivity extends AppCompatActivity
    implements MapFragment.OnMapChangeActivityListener {

    MapFragment mapFragment;
    String selectedEventID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mapFragment = new MapFragment();
        mapFragment.setHasOptionsMenu(false);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mapFragment)
                .commit();

        Intent intent = getIntent();
        selectedEventID = intent.getStringExtra("eventID");
    }

    @Override
    public ProgressDialog getLoadingDialog() {
        return null;
    }

    /**
     * Called when person card is clicked.
     * @param eventID selected event.
     */
    @Override
    public void onCardClick(String eventID) {
        Event event = FamilyReunion.getInstance().getEventIDtoEvent().get(eventID);

        Intent intent = new Intent(this, PersonActivity.class);
        intent.putExtra("personID", event.getPersonID());
        startActivity(intent);
    }

    /**
     * Called when map loads, and centers on event.
     */
    @Override
    public void onMapLoad() {
        mapFragment.centerOnEvent(selectedEventID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.person_options, menu);
        menu.findItem(R.id.top_icon).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_angle_double_up)
                        .colorRes(android.R.color.white)
                        .actionBarSize()).setVisible(true);
        return true;
    }

    /**
     * On Up click, returns to prev screen.
     * On Go to Top, returns to Main map.
     * @param item button clicked.
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.top_icon:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
