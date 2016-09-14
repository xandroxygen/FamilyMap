package com.moffatt.xander.familymap.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;
import android.util.JsonReader;
import android.util.JsonWriter;

import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.model.Filter;
import com.moffatt.xander.familymap.model.Line;
import com.moffatt.xander.familymap.model.LineSetting;
import com.moffatt.xander.familymap.model.MapSetting;
import com.moffatt.xander.familymap.model.Person;
import com.moffatt.xander.familymap.model.Setting;
import com.moffatt.xander.familymap.model.User;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles specific user requests to server, including
 * Login request to authenticate user, and
 * Data request for all persons and events.
 * Created by Xander on 7/26/2016.
 */
public class RequestController extends AsyncRequest {

    private Map<String, String> headers;
    private Context context;

    public RequestController(String host, String port, Context context) {
        super(host, port);
        this.context = context;
        headers = new HashMap<>();
    }

    // --- REQUEST FUNCTIONS --- //

    /**
     * Logs user in by starting async request.
     * @param username
     * @param password
     * @param proxy The interface to call back to when completed.
     * @return false if there is no network.
     */
    public boolean authenticateUser(String username, String password, LoginProxy proxy) {

        String urlExt = "/user/login";
        String postData = null;

        try {
            StringWriter writer = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(writer);

            jsonWriter.beginObject();
            jsonWriter.name("username").value(username);
            jsonWriter.name("password").value(password);
            jsonWriter.endObject();

            jsonWriter.flush();
            postData = writer.toString();
        }
        catch (IOException e) {
            // do nothing?
        }

        if (isThereANetworkOrWhat()) {
            POSTAsync post = new POSTAsync();
            post.execute(urlExt, postData, headers, proxy);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Requests all person data from the server.
     * @param authToken
     * @param proxy The interface to call back to when completed.
     */
    public void getAllPeople(String authToken, LoginProxy proxy) {

        String urlExt = "/person/";

        if (isThereANetworkOrWhat()) {
            GETAsync get = new GETAsync();
            headers.clear();
            headers.put("Authorization", authToken);
            get.execute(urlExt, headers, proxy);
        }
    }

    /**
     * Requests all event data from the server.
     * @param authToken
     * @param proxy The interface to call back to when completed.
     */
    public void getAllEvents(String authToken, LoginProxy proxy) {

        String urlExt = "/event/";

        if (isThereANetworkOrWhat()) {
            GETAsync get = new GETAsync();
            headers.clear();
            headers.put("Authorization", authToken);
            get.execute(urlExt, headers, proxy);
        }
    }

    // --- DATA INITIALIZATION FUNCTIONS --- //

    /**
     * Parses JSON response to user login.
     * @param data JSON from /user/login request
     * @return Response, whether success or error.
     */
    public String[] decodeLoginResponse(String data) {
        String[] response = new String[3];
        JsonReader reader = new JsonReader(new StringReader(data));

        try {
            reader.beginObject();

            while (reader.hasNext()) {
                String type = reader.nextName();
                switch (type) {
                    case "Authorization":
                        response[0] = reader.nextString();
                        break;
                    case "userName":
                        response[1] = reader.nextString();
                        break;
                    case "personId":
                        response[2] = reader.nextString();
                        break;
                    default:
                        // invalid login
                        response[0] = "invalid";
                        reader.skipValue();
                        break;
                }
            }

            reader.endObject();
        }
        catch (IOException e) {
            // do nothing?
        }
        return response;
    }

    /**
     * Parses JSON response to get all people, and creates Person models.
     * @param data JSON from /person request
     */
    public void populateModelWithPersons(String data) {
        JsonReader jsonReader = new JsonReader(new StringReader(data));

        try {
            jsonReader.beginObject(); // begin
            jsonReader.nextName(); // skip "data"
            jsonReader.beginArray(); // begin data

            while (jsonReader.hasNext()) {
                jsonReader.beginObject(); // begin person
                Person person = new Person();

                while (jsonReader.hasNext()) {

                    String prop = jsonReader.nextName();
                    switch (prop) {
                        case "descendant":
                            person.setDescendantID(jsonReader.nextString());
                            break;
                        case "personID":
                            person.setID(jsonReader.nextString());
                            break;
                        case "firstName":
                            person.setFirstName(jsonReader.nextString());
                            break;
                        case "lastName":
                            person.setLastName(jsonReader.nextString());
                            break;
                        case "gender":
                            person.setGender(jsonReader.nextString());
                            break;
                        case "father":
                            person.setFatherID(jsonReader.nextString());
                            break;
                        case "mother":
                            person.setMotherID(jsonReader.nextString());
                            break;
                        case "spouse":
                            person.setSpouseID(jsonReader.nextString());
                            break;
                        default:
                            jsonReader.skipValue();
                            break;
                    }
                }
                // check for current user
                if (person.getID().equals(FamilyReunion.getInstance().getCurrentUser().getID())) {
                    User currentUser = FamilyReunion.getInstance().getCurrentUser();

                    currentUser.setDescendantID(person.getDescendantID());
                    currentUser.setFirstName(person.getFirstName());
                    currentUser.setLastName(person.getLastName());
                    currentUser.setGender(person.getGender());
                    currentUser.setFatherID(person.getFatherID());
                    currentUser.setMotherID(person.getMotherID());
                    currentUser.setSpouseID(person.getSpouseID());

                    FamilyReunion.getInstance().getPeople().add(currentUser);
                    FamilyReunion.getInstance().getPersonIDtoPerson().put(currentUser.getID(), currentUser);
                }
                else {
                    FamilyReunion.getInstance().getPeople().add(person);
                    FamilyReunion.getInstance().getPersonIDtoPerson().put(person.getID(), person);
                }
                jsonReader.endObject(); // end Person
            }

            jsonReader.endArray(); // end Data
            jsonReader.endObject(); // end



        }
        catch (IOException e) {
            // do nothing?
        }
    }

    /**
     * Parses JSON response to get all events, and creates Event models.
     * @param data JSON from /events request
     */
    public void populateModelWithEvents(String data) {
        JsonReader jsonReader = new JsonReader(new StringReader(data));

        try {
            jsonReader.beginObject(); // begin
            jsonReader.nextName(); // skip "data"
            jsonReader.beginArray(); // begin data

            while (jsonReader.hasNext()) {
                jsonReader.beginObject(); // begin event
                Event event = new Event();

                while (jsonReader.hasNext()) {

                    String prop = jsonReader.nextName();
                    switch (prop) {
                        case "eventID":
                            event.setEventID(jsonReader.nextString());
                            break;
                        case "personID":
                            event.setPersonID(jsonReader.nextString());
                            break;
                        case "latitude":
                            event.setLatitude(jsonReader.nextDouble());
                            break;
                        case "longitude":
                            event.setLongitude(jsonReader.nextDouble());
                            break;
                        case "country":
                            event.setCountry(jsonReader.nextString());
                            break;
                        case "city":
                            event.setCity(jsonReader.nextString());
                            break;
                        case "description":
                            event.setDescription(jsonReader.nextString());
                            break;
                        case "year":
                            event.setYear(jsonReader.nextString());
                            break;
                        case "descendant":
                            event.setDescendant(jsonReader.nextString());
                            break;
                        default:
                            jsonReader.skipValue();
                            break;
                    }
                }
                FamilyReunion.getInstance().getEvents().add(event);
                FamilyReunion.getInstance().getEventIDtoEvent().put(event.getEventID(), event);
                jsonReader.endObject(); // end Event
            }

            jsonReader.endArray(); // end Data
            jsonReader.endObject(); // end



        }
        catch (IOException e) {
            // do nothing?
        }
    }

    /**
     * Creates map of person ID to list of events, for quick reference.
     */
    public void mapPersonsToEvents() {

        for (Person person : FamilyReunion.getInstance().getPeople()) {
            ArrayList<Event> events = new ArrayList<>();
            for (Event event : FamilyReunion.getInstance().getEvents()) {
                if (event.getPersonID().equals(person.getID())) {
                    events.add(event);
                }
            }

            //sort events
            Collections.sort(events, Event.eventComparator);

            FamilyReunion.getInstance().getPersonIDToEvents().put(person.getID(), events);
        }
    }

    /**
     * Adds ID of children to father and mother of each person.
     */
    public void mapChildrenToParents() {

        for (Person person : FamilyReunion.getInstance().getPeople()) {

            if (person.getFatherID() != null) {
                Person father = FamilyReunion.getInstance().getPersonIDtoPerson().get(person.getFatherID());
                father.getChildrenIDs().add(person.getID());
            }

            if (person.getMotherID() != null) {
                Person mother = FamilyReunion.getInstance().getPersonIDtoPerson().get(person.getMotherID());
                mother.getChildrenIDs().add(person.getID());
            }
        }
    }

    /**
     * Adds settings to the model, for lines and for map type.
     */
    public void constructDefaultSettings() {

        LineSetting lifeStory = new LineSetting(Color.GREEN, true, Setting.LINE_TYPE.LIFE_STORY);
        LineSetting familyTree = new LineSetting(Color.BLUE, true, Setting.LINE_TYPE.FAMILY_TREE);
        LineSetting spouse = new LineSetting(Color.RED, true, Setting.LINE_TYPE.SPOUSE);

        MapSetting mapType = new MapSetting(Setting.MAP_TYPE.NORMAL);

        FamilyReunion.getInstance().getSettings().add(lifeStory);
        FamilyReunion.getInstance().getSettings().add(familyTree);
        FamilyReunion.getInstance().getSettings().add(spouse);
        FamilyReunion.getInstance().getSettings().add(mapType);
    }

    /**
     * Creates necessary filters - for events, gender, and family side.
     * Reads saved preferences and constructs filters using those.
     */
    public void constructFilterEventList() {

        ArrayList<Integer> colors = populateColors();
        SharedPreferences filterPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);

        // event filters
        for (Event event : FamilyReunion.getInstance().getEvents()) {
            boolean descMatches = false;

            for (Filter filter : FamilyReunion.getInstance().getFilters()) {
                if (filter.getDescription().compareToIgnoreCase(event.getDescription()) == 0) {
                    descMatches = true;
                }
            }

            if (!descMatches) {
                boolean isDrawn = filterPreferences.getBoolean(event.getDescription(), true);
                Filter filter = new Filter(isDrawn, event.getDescription(), colors.get(0));
                colors.remove(0); // pop colors array
                FamilyReunion.getInstance().getFilters().add(filter);
            }
        }

        // gender filters
        boolean isDrawn = filterPreferences.getBoolean("m", true);
        Filter male = new Filter(isDrawn, "m", colors.get(0));
        colors.remove(0);
        FamilyReunion.getInstance().getFilters().add(male);

        isDrawn = filterPreferences.getBoolean("f", true);
        Filter female = new Filter(isDrawn, "f", colors.get(0));
        colors.remove(0);
        FamilyReunion.getInstance().getFilters().add(female);

        // family filters
        isDrawn = filterPreferences.getBoolean("father", true);
        Filter father = new Filter(isDrawn, "father", colors.get(0));
        colors.remove(0);
        FamilyReunion.getInstance().getFilters().add(father);

        isDrawn = filterPreferences.getBoolean("mother", true);
        Filter mother = new Filter(isDrawn, "mother", colors.get(0));
        colors.remove(0);
        FamilyReunion.getInstance().getFilters().add(mother);
    }

    /**
     * for each event, finds matching filter.
     * Also populates gender and family side filters.
     */
    public void populateFiltersWithEvents() {

        Filter fatherSide = null;
        Filter motherSide = null;

        for (Event event : FamilyReunion.getInstance().getEvents()) {

            Person person  = FamilyReunion.getInstance().getPersonIDtoPerson().get(event.getPersonID());

            for (Filter filter : FamilyReunion.getInstance().getFilters()) {

                // event filters
                if (filter.getDescription().equals(event.getDescription())) {
                    filter.getAssociatedEventIDs().add(event.getEventID());
                }

                // gender filters
                else if (filter.getDescription().equals(person.getGender())) {
                    filter.getAssociatedEventIDs().add(event.getEventID());
                }

                // find family filters
                else if (filter.getDescription().equals("father") &&
                        fatherSide == null) {
                    fatherSide = filter;
                }
                else if (filter.getDescription().equals("mother") &&
                        motherSide == null) {
                    motherSide = filter;
                }
            }
        }

        // family side filters
        ArrayList<String> fatherSideEventIDs = new ArrayList<>();
        ArrayList<String> motherSideEventIDs = new ArrayList<>();
        User currentUser = FamilyReunion.getInstance().getCurrentUser();

        if (currentUser.getFatherID() != null) {
            addEventsToTreeFilter(currentUser.getFatherID(), fatherSideEventIDs);
            fatherSide.getAssociatedEventIDs().addAll(fatherSideEventIDs);
        }

        if (currentUser.getMotherID() != null) {
            addEventsToTreeFilter(currentUser.getMotherID(), motherSideEventIDs);
            motherSide.getAssociatedEventIDs().addAll(motherSideEventIDs);
        }
    }

    /**
     * Recursive helper function for populating family side filters.
     * Moves up the tree adding events to filters.
     * @param personID current person
     * @param events events to add to the filter.
     */
    public void addEventsToTreeFilter(String personID, ArrayList<String> events) {

        Person person = null;
        if (personID != null) {
            person = FamilyReunion.getInstance().getPersonIDtoPerson().get(personID);
        }
        if (person != null) {
            for (Event event : FamilyReunion.getInstance().getPersonIDToEvents().get(personID)) {
                events.add(event.getEventID());
            }

            String fatherID = person.getFatherID();
            if (fatherID != null) {
                addEventsToTreeFilter(fatherID, events);
            }

            String motherID = person.getMotherID();
            if (motherID != null) {
                addEventsToTreeFilter(motherID, events);
            }
        }
    }

    /**
     * Creates list of color ints for use in dynamically constructing filters.
     * @return
     */
    private ArrayList<Integer> populateColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.GRAY);
        colors.add(Color.BLACK);
        colors.add(Color.WHITE);
        colors.add(Color.LTGRAY);
        colors.add(Color.DKGRAY);
        return colors;
    }

    // --- HELPERS --- //

    /**
     * Checks if network is available and connected
     * @return boolean status of network connection
     */
    private boolean isThereANetworkOrWhat() {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeConManInfo = conMan.getActiveNetworkInfo();
        return activeConManInfo != null && activeConManInfo.isConnected();
    }

    /**
     * Asynchronous request class that works in background.
     * Calls GET and waits for response.
     * Pings fragment once request is received.
     * param: String url extension
     * param: Map of headers if needed
     * param: LoginProxy proxy to call response on
     */
    private class GETAsync extends AsyncTask<Object, Integer, RequestResponse> {

        private LoginProxy proxy;

        @Override
        protected RequestResponse doInBackground(Object... params) {
            proxy = (LoginProxy) params[2];

            try {
                return GET((String)params[0], (Map<String,String>)params[1]);
            }
            catch (MalformedURLException e) {
                return new RequestResponse(true, e);
            }
        }

        @Override
        protected void onPostExecute(RequestResponse requestResponse) {
            proxy.onRequestResponse(requestResponse);
        }
    }

    /**
     * Asynchronous request class that works in background.
     * Calls POST and waits for response.
     * Pings fragment once request is received.
     * param: String url extension
     * param: String postData - data to send to server
     * param: Map of headers if needed
     * param: LoginProxy proxy to call response on
     */
    private class POSTAsync extends AsyncTask<Object, Integer, RequestResponse> {
        private LoginProxy proxy;

        @Override
        protected RequestResponse doInBackground(Object... params) {
            proxy = (LoginProxy) params[3];

            try {
                return POST((String)params[0], (String)params[1], (Map<String,String>)params[2]);
            }
            catch (MalformedURLException e) {
                return new RequestResponse(true, e);
            }
        }

        @Override
        protected void onPostExecute(RequestResponse requestResponse) {
            proxy.onRequestResponse(requestResponse);
        }
    }
}