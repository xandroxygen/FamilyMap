package com.moffatt.xander.familymap.model;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Event model class - stores event info.
 * Created by canuck24 on 7/27/16.
 */
public class Event {
    private String eventID;
    private String personID;
    private double latitude;
    private double longitude;
    private String country;
    private String city;
    private String description;
    private String year;
    private String descendant;

    private ArrayList<Line> lines;

    public Event() {
        eventID = new String();
        personID = new String();
        country = new String();
        city = new String();
        description = new String();
        year = new String();
        descendant = new String();
        lines = new ArrayList<>();
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDescendant() {
        return descendant;
    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return getEventID().equals(event.getEventID());

    }

    @Override
    public int hashCode() {
        return getEventID().hashCode();
    }

    public static Comparator<Event> eventComparator = new Comparator<Event>() {
        @Override
        public int compare(Event a, Event b) {

            // if birth, always first
            if (a.getDescription().equals("birth")) {
                return -1;
            }
            else if (b.getDescription().equals("birth")) {
                return 1;
            }

            // if death, always last
            if (a.getDescription().equals("death")) {
                return 1;
            }
            else if(b.getDescription().equals("death")) {
                return -1;
            }

            // if both have a year, sort by that
            if (a.getYear() != null && b.getYear() != null) {
                int aYear = Integer.parseInt(a.getYear());
                int bYear = Integer.parseInt(b.getYear());
                return (aYear < bYear ? -1 :
                        aYear == bYear ? 0 : 1);
            }

            // otherwise, sort by description
            return a.getDescription().compareToIgnoreCase(b.getDescription());
        }
    };
}
