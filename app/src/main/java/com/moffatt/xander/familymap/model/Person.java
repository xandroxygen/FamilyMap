package com.moffatt.xander.familymap.model;

import java.util.ArrayList;

/**
 * Model class for a Person in the family map.
 * Contains data about person such as ID,
 * first name, last name, descendant, gender, father, mother, spouse.
 * All event data is stored in Events associated with ID.
 * Created by Xander on 7/27/2016.
 */
public class Person {
    private String ID;
    private String firstName;
    private String lastName;
    private String descendantID;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    private ArrayList<Line> lines;
    private ArrayList<String> childrenIDs;

    public Person() {
        lines = new ArrayList<>();
        childrenIDs = new ArrayList<>();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescendantID() {
        return descendantID;
    }

    public void setDescendantID(String descendantID) {
        this.descendantID = descendantID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public ArrayList<String> getChildrenIDs() {
        return childrenIDs;
    }

    public void setChildrenIDs(ArrayList<String> childrenIDs) {
        this.childrenIDs = childrenIDs;
    }
}
