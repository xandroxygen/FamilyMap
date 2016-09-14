package com.moffatt.xander.familymap.person;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.IconTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.base.MainActivity;
import com.moffatt.xander.familymap.map.MapActivity;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.model.Filter;
import com.moffatt.xander.familymap.model.Person;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity
        implements EventAdapter.EventClickCallback, PersonAdapter.PersonClickCallback {

    EditText searchBox;
    ArrayList<Event> filteredEvents;
    ArrayList<Person> allPeople;
    ArrayList<Event> matchingEvents;
    ArrayList<Person> matchingPeople;

    RecyclerView eventRecycler;
    RecyclerView personRecycler;
    EventAdapter eventAdapter;
    PersonAdapter personAdapter;
    RecyclerView.LayoutManager eventLM;
    RecyclerView.LayoutManager personLM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setTitle("FamilyMap - Search");

        filteredEvents = new ArrayList<>();
        allPeople = new ArrayList<>();
        matchingEvents = new ArrayList<>();
        matchingPeople = new ArrayList<>();

        eventRecycler = (RecyclerView)findViewById(R.id.s_event_recycler);
        personRecycler = (RecyclerView)findViewById(R.id.s_person_recycler);

        eventLM = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        eventRecycler.setLayoutManager(eventLM);

        personLM = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        personRecycler.setLayoutManager(personLM);

        // adapters are set after data is retrieved below

        populateEventsAndPeople();

        updateCardVisibility();

        searchBox = (EditText)findViewById(R.id.search_text);
        final EditText focusThief = (EditText)findViewById(R.id.focus_thief);
        searchBox.requestFocus();
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = v.getText().toString();
                    onSearch(searchText);
                    handled = true;
                }
                return handled;
            }
        });
    }

    /**
     * Gets lists of filtered events and people to search through.
     */
    public void populateEventsAndPeople() {

        filteredEvents = FamilyReunion.getInstance().getFilteredEvents();
         allPeople = FamilyReunion.getInstance().getPeople();
    }

    /**
     * Called on search. Searches through people and filtered events,
     * and displays matching results.
     * @param searchText user-entered text to search for.
     */
    public void onSearch(String searchText) {

        matchingEvents = new ArrayList<>();
        matchingPeople = new ArrayList<>();
        searchText = searchText.toLowerCase();

        for (Person person : allPeople) {
            String first = person.getFirstName().toLowerCase();
            String last = person.getLastName().toLowerCase();

            if (first.contains(searchText) ||
                    last.contains(searchText)) {
                matchingPeople.add(person);
            }
        }

        for (Event event : filteredEvents) {
            String country = event.getCountry().toLowerCase();
            String city = event.getCity().toLowerCase();
            String description = event.getDescription().toLowerCase();
            String year = event.getYear().toLowerCase();

            if (country.contains(searchText) ||
                    city.contains(searchText) ||
                    description.contains(searchText) ||
                    year.contains(searchText)) {
                matchingEvents.add(event);
            }
        }

        boolean resultsFound = updateCardVisibility();
        if (!resultsFound) {
            showCenteredToast("No results found.");
        }

        updateEventAdapter();

        updatePersonAdapter();
    }

    /**
     * Helper function: Hides cards if there are no results.
     * @return true if there are results
     */
    public boolean updateCardVisibility() {

        boolean noEvents = false;
        boolean noPeople = false;

        CardView events = (CardView) findViewById(R.id.s_event_card);
        if (matchingEvents.size() <= 0) {
            events.setVisibility(View.GONE);
            noEvents = true;
        } else {
            events.setVisibility(View.VISIBLE);
        }

        CardView people = (CardView) findViewById(R.id.s_person_card);
        if (matchingPeople.size() <= 0) {
            people.setVisibility(View.GONE);
            noPeople = true;
        }
        else {
            people.setVisibility(View.VISIBLE);
        }

        if (noEvents && noPeople) {
            return false;
        }
        return true;
    }

    /**
     * Updates events list on screen,
     * and resizes card to match.
     */
    public void updateEventAdapter() {
        ArrayList<ParentListEvent> parentEvents = new ArrayList<>();
        parentEvents.add(new ParentListEvent(matchingEvents));

        final CardView eventCard = (CardView) findViewById(R.id.s_event_card);
        final int collapsedCardHeight = 60;
        final int expandedCardHeight = (matchingEvents.size() * 80) + 50;

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
     * Updates person list on screen,
     * and resizes card to match.
     */
    public void updatePersonAdapter() {
        ArrayList<ParentListPerson> people = new ArrayList<>();
        people.add(new ParentListPerson(matchingPeople));

        final CardView personCard = (CardView) findViewById(R.id.s_person_card);
        final int pCollapsedCardHeight = 60;
        final int pExpandedCardHeight = (matchingPeople.size() * 80) + 50;

        LinearLayout.LayoutParams pParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pExpandedCardHeight);
        pParams.setMargins(8,0,8,8);
        personCard.setLayoutParams(pParams);

        personAdapter = new PersonAdapter(this, people, null);
        ExpandableRecyclerAdapter.ExpandCollapseListener personExpansionListener = new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pExpandedCardHeight);
                params.setMargins(8,0,8,8);
                personCard.setLayoutParams(params);
                IconTextView icon = (IconTextView)personCard.findViewById(R.id.card_title_icon);
                icon.setText(R.string.icon_chevron_down);
            }

            @Override
            public void onListItemCollapsed(int position) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pCollapsedCardHeight);
                params.setMargins(8,0,8,8);
                personCard.setLayoutParams(params);
                IconTextView icon = (IconTextView)personCard.findViewById(R.id.card_title_icon);
                icon.setText(R.string.icon_chevron_right);
            }
        };
        personAdapter.setExpandCollapseListener(personExpansionListener);

        personRecycler.setAdapter(personAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showCenteredToast(String toastText) {
        Toast toast = Toast.makeText(this, toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    /**
     * Called by event callback in Event Adapter when a child is clicked.
     */
    @Override
    public void onEventClick(String eventID) {
        Intent intent = new Intent(SearchActivity.this, MapActivity.class);
        intent.putExtra("eventID", eventID);
        startActivity(intent);
    }

    /**
     * Called by person callback in Person Adapter when a child is clicked.
     */
    @Override
    public void onPersonClick(String personID) {
        Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
        intent.putExtra("personID", personID);
        startActivity(intent);
    }
}
