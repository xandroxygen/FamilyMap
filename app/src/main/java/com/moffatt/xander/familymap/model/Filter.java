package com.moffatt.xander.familymap.model;

import com.moffatt.xander.familymap.base.FamilyReunion;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Settings for filters for list of events.
 * Created by Xander on 7/28/2016.
 */
public class Filter {
    private boolean isSelected;
    private String description;
    private int color;
    ArrayList<String> associatedEventIDs;

    public Filter() {
        isSelected = false;
        description = "";
        color = 0;
        associatedEventIDs = new ArrayList<>();

        if (isSelected) {
            FamilyReunion.getInstance().enableFilter(this);
        }
        else {
            FamilyReunion.getInstance().disableFilter(this);
        }
    }

    public Filter(boolean isSelected, String description, int color) {
        this.isSelected = isSelected;
        this.description = description;
        this.color = color;
        associatedEventIDs = new ArrayList<>();

        if (isSelected) {
            FamilyReunion.getInstance().enableFilter(this);
        }
        else {
            FamilyReunion.getInstance().disableFilter(this);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<String> getAssociatedEventIDs() {
        return associatedEventIDs;
    }

    public void setAssociatedEventIDs(ArrayList<String> associatedEventIDs) {
        this.associatedEventIDs = associatedEventIDs;
    }
}
