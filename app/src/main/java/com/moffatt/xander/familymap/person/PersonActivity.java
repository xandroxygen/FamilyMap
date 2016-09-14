package com.moffatt.xander.familymap.person;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.IconTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.base.MainActivity;
import com.moffatt.xander.familymap.map.MapActivity;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.model.Filter;
import com.moffatt.xander.familymap.model.Person;

import java.util.ArrayList;

/**
 * Displays person description, list of events, and list of family.
 * Called by card click in Map Fragment.
 * Calls Map Activity on event click, and Person Activity on person click.
 * Created by Xander on 8/1/2016.
 */
public class PersonActivity extends AppCompatActivity
    implements EventAdapter.EventClickCallback, PersonAdapter.PersonClickCallback {

    RecyclerView eventRecycler;
    RecyclerView familyRecycler;
    EventAdapter eventAdapter;
    PersonAdapter familyAdapter;
    RecyclerView.LayoutManager eventLM;
    RecyclerView.LayoutManager familyLM;

    String selectedPersonID;
    Person selectedPerson;
    ArrayList<Event> selectedEvents;
    ArrayList<Person> selectedFamily;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        getSupportActionBar().setTitle("FamilyMap - Person Details");

        eventRecycler = (RecyclerView) findViewById(R.id.event_recycler);
        familyRecycler = (RecyclerView) findViewById(R.id.person_recycler);

        eventLM = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        eventRecycler.setLayoutManager(eventLM);

        familyLM = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        familyRecycler.setLayoutManager(familyLM);


        // adapters are set after data is retrieved below

        Intent intent = getIntent();
        selectedPersonID = intent.getStringExtra("personID");

        populateDescription();

        populateEvents();

        populateFamily();
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

    /**
     * Sets person description in the view.
     */
    public void populateDescription() {
        IconTextView iconTextView = (IconTextView)findViewById(R.id.p_desc_icon);
        TextView nameView = (TextView)findViewById(R.id.p_desc_name);
        TextView genderView = (TextView)findViewById(R.id.p_desc_gender);

        selectedPerson = FamilyReunion.getInstance().getPersonIDtoPerson().get(selectedPersonID);

        String name = selectedPerson.getFirstName() + " " + selectedPerson.getLastName();
        nameView.setText(name);

        if (selectedPerson.getGender().equals("m")) {
            iconTextView.setText(R.string.info_card_icon_male);
            genderView.setText(R.string.male);
        }
        else {
            iconTextView.setText(R.string.info_card_icon_female);
            genderView.setText(R.string.female);
        }
        iconTextView.setTextColor(Color.GREEN);


    }

    /**
     * Displays filtered events for the selected person.
     * Responds to event list expansion/collapse,
     * and updates card size to match.
     */
    public void populateEvents() {

        ArrayList<Event> personEvents = FamilyReunion.getInstance().getPersonIDToEvents().get(selectedPersonID);
        ArrayList<Event> filteredEvents = FamilyReunion.getInstance().getFilteredEvents();
        selectedEvents = new ArrayList<>();

        for (Event event : personEvents) {
            if (filteredEvents.contains(event)) {
                selectedEvents.add(event);
            }
        }


        ArrayList<ParentListEvent> parentEvents = new ArrayList<>();
        parentEvents.add(new ParentListEvent(selectedEvents));

        final CardView eventCard = (CardView) findViewById(R.id.p_event_card);
        final int collapsedCardHeight = 60;
        final int expandedCardHeight = (selectedEvents.size() * 80) + 50;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, expandedCardHeight);
        params.setMargins(8,0,8,8);
        eventCard.setLayoutParams(params);

        eventAdapter = new EventAdapter(this, parentEvents);
        ExpandableRecyclerAdapter.ExpandCollapseListener eventExpansionListener = new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, expandedCardHeight);
                params.setMargins(8,0,8,8);
                eventCard.setLayoutParams(params);
                IconTextView icon = (IconTextView)eventCard.findViewById(R.id.card_title_icon);
                icon.setText(R.string.icon_chevron_down);
            }

            @Override
            public void onListItemCollapsed(int position) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, collapsedCardHeight);
                params.setMargins(8,0,8,8);
                eventCard.setLayoutParams(params);
                IconTextView icon = (IconTextView)eventCard.findViewById(R.id.card_title_icon);
                icon.setText(R.string.icon_chevron_right);
            }
        };
        eventAdapter.setExpandCollapseListener(eventExpansionListener);



        eventRecycler.setAdapter(eventAdapter);
    }

    /**
     * Displays family members for the selected person.
     * Responds to family list expansion/collapse,
     * and updates card size to match.
     */
    public void populateFamily() {
        selectedFamily = new ArrayList<>();
        Person father = FamilyReunion.getInstance().getPersonIDtoPerson().get(selectedPerson.getFatherID());
        if (father != null) {
            selectedFamily.add(father);
        }
        Person mother = FamilyReunion.getInstance().getPersonIDtoPerson().get(selectedPerson.getMotherID());
        if (mother != null) {
            selectedFamily.add(mother);
        }
        if (selectedPerson.getChildrenIDs().size() > 0) {
            for (String childID : selectedPerson.getChildrenIDs()) {
                Person child = FamilyReunion.getInstance().getPersonIDtoPerson().get(childID);
                if (child != null) {
                    selectedFamily.add(child);
                }
            }
        }

        ArrayList<ParentListPerson> family = new ArrayList<>();
        family.add(new ParentListPerson(selectedFamily));

        final CardView familyCard = (CardView) findViewById(R.id.p_person_card);
        final int collapsedCardHeight = 60;
        final int expandedCardHeight = (selectedFamily.size() * 80) + 50;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, expandedCardHeight);
        params.setMargins(8,0,8,8);
        familyCard.setLayoutParams(params);

        familyAdapter = new PersonAdapter(this, family, selectedPerson);
        ExpandableRecyclerAdapter.ExpandCollapseListener eventExpansionListener = new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, expandedCardHeight);
                params.setMargins(8,0,8,8);
                familyCard.setLayoutParams(params);
                IconTextView icon = (IconTextView)familyCard.findViewById(R.id.card_title_icon);
                icon.setText(R.string.icon_chevron_down);
            }

            @Override
            public void onListItemCollapsed(int position) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, collapsedCardHeight);
                params.setMargins(8,0,8,8);
                familyCard.setLayoutParams(params);
                IconTextView icon = (IconTextView)familyCard.findViewById(R.id.card_title_icon);
                icon.setText(R.string.icon_chevron_right);
            }
        };
        familyAdapter.setExpandCollapseListener(eventExpansionListener);

        familyRecycler.setAdapter(familyAdapter);
    }

    /**
     * Called by event callback in Event Adapter when a child is clicked.
     * Starts new Map Activity with event centered.
     */
    @Override
    public void onEventClick(String eventID) {
        Intent intent = new Intent(PersonActivity.this, MapActivity.class);
        intent.putExtra("eventID", eventID);
        startActivity(intent);
    }

    /**
     * Called by person callback in Person Adapter when a child is clicked.
     * Starts new Person Activity.
     */
    @Override
    public void onPersonClick(String personID) {
        Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
        intent.putExtra("personID", personID);
        startActivity(intent);
    }
}
