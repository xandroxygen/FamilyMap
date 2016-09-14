package com.moffatt.xander.familymap.base;

import com.moffatt.xander.familymap.login.RequestController;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.model.Filter;
import com.moffatt.xander.familymap.model.Person;
import com.moffatt.xander.familymap.model.Setting;
import com.moffatt.xander.familymap.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Datastore singleton. Contains the current user and its credentials,
 * and all of the family data returned from the server.
 * Created by Xander on 7/27/2016.
 */
public class FamilyReunion {

    private User currentUser;
    private RequestController controller;
    private ArrayList<Person> people;
    private ArrayList<Event> events;
    private Map<String, ArrayList<Event>> personIDToEvents;
    private Map<String, Person> personIDtoPerson;
    private Map<String, Event> eventIDtoEvent;
    private ArrayList<Setting> settings;
    private ArrayList<Filter> filters;
    private ArrayList<Filter> enabledFilters;
    private ArrayList<Filter> disabledFilters;
    private ArrayList<Event> filteredEvents;

    private static FamilyReunion ourInstance = new FamilyReunion();

    public static FamilyReunion getInstance() {
        return ourInstance;
    }

    private FamilyReunion() {
        currentUser = new User();
        people = new ArrayList<>();
        events = new ArrayList<>();
        personIDToEvents = new HashMap<>();
        personIDtoPerson = new HashMap<>();
        eventIDtoEvent = new HashMap<>();
        settings = new ArrayList<>();
        filters = new ArrayList<>();
        enabledFilters = new ArrayList<>();
        disabledFilters = new ArrayList<>();
        filteredEvents = new ArrayList<>();
    }

    /**
     * Used to clear out old data after a login and before syncing data
     * Keeps user data if option passed in is true. Otherwise, deletes user data as well.
     * User data is username, password, authToken, and ID.
     */
    public void clear(boolean clearUser) {
        User user = currentUser;
        String username = user.getUsername();
        String password = user.getPassword();
        String authToken = user.getAuthToken();
        String userID = user.getID();

        ourInstance = new FamilyReunion();
        if (!clearUser) {
            ourInstance.currentUser = new User();
            ourInstance.currentUser.setUsername(username);
            ourInstance.currentUser.setPassword(password);
            ourInstance.currentUser.setAuthToken(authToken);
            ourInstance.currentUser.setID(userID);
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public Map<String, ArrayList<Event>> getPersonIDToEvents() {
        return personIDToEvents;
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public Map<String, Person> getPersonIDtoPerson() {
        return personIDtoPerson;
    }

    public ArrayList<Filter> getFilters() {
        return filters;
    }

    public ArrayList<Filter> getEnabledFilters() { return enabledFilters; }

    public Map<String, Event> getEventIDtoEvent() {
        return eventIDtoEvent;
    }

    public RequestController getController() {
        return controller;
    }

    public void setController(RequestController controller) {
        this.controller = controller;
    }

    // --- HELPER FUNCTIONS --- //

    /**
     * Searches given person for birth event, or most recent event.
     * @param p person to look for events
     * @return eventID of birth/most recent event
     */
    public String findBirthOrMostRecentEvent(Person p) {

        int mostRecentYear = 2016;
        Event mostRecent = null;

        for (Event event : FamilyReunion.getInstance().getPersonIDToEvents().get(p.getID())) {
            if (event.getDescription().equals("birth")) {
                return event.getEventID();
            }
            else if (Integer.parseInt(event.getYear()) <= mostRecentYear) {
                mostRecentYear = Integer.parseInt(event.getYear());
                mostRecent = event;
            }
        }

        if (mostRecent != null) {
            return mostRecent.getEventID();
        }
        return null;
    }

    /**
     * Enables filter for drawing events.
     * @param filter to enable
     */
    public void enableFilter(Filter filter) {

        boolean notEnabled  = true;
        for (Filter enabled : enabledFilters) {
            if (enabled.getDescription().equals(filter.getDescription())) {
                notEnabled = false;
            }
        }
        if (notEnabled) {
            enabledFilters.add(filter);
            filter.setSelected(true);
        }

        boolean notDisabled = false;
        for (Filter disabled : disabledFilters) {
            if (disabled.getDescription().equals(filter.getDescription())) {
                notDisabled = true;
            }
        }
        if (notDisabled) {
            disabledFilters.remove(filter);
        }
    }

    /**
     * Disables filter for drawing events.
     * @param filter to disable
     */
    public void disableFilter(Filter filter) {

        boolean notDisabled = true;
        for (Filter disabled : disabledFilters) {
            if (disabled.getDescription().equals(filter.getDescription())) {
                notDisabled = false;
            }
        }
        if (notDisabled) {
            disabledFilters.add(filter);
            filter.setSelected(false);
        }

        boolean notEnabled  = false;
        for (Filter enabled : enabledFilters) {
            if (enabled.getDescription().equals(filter.getDescription())) {
                notEnabled = true;
            }
        }
        if (notEnabled) {
            enabledFilters.remove(filter);
        }
    }

    /**
     * Checks that all filters for each event are enabled.
     * @return A list of events that have all filters enabled.
     */
    public ArrayList<Event> getFilteredEvents() {
        ArrayList<Event> ret = new ArrayList<>();

        for (Filter enabled : enabledFilters) {
            for (String eventID : enabled.getAssociatedEventIDs()) {
                Event event = getEventIDtoEvent().get(eventID);
                if (!ret.contains(event)) {
                    ret.add(event);
                }
            }
        }

        ArrayList<Event> toRemove = new ArrayList<>();
        for (Filter disabled : disabledFilters) {
            for (String eventID : disabled.getAssociatedEventIDs()) {
                Event event = getEventIDtoEvent().get(eventID);
                if (ret.contains(event)) {
                    toRemove.add(event);
                }
            }
        }
        ret.removeAll(toRemove);

        return ret;
    }
}