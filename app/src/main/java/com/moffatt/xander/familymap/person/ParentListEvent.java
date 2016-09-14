package com.moffatt.xander.familymap.person;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.moffatt.xander.familymap.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for parent of event list
 * Created by Xander on 8/1/2016.
 */
public class ParentListEvent implements ParentListItem {

    ArrayList<Event> children;

    public ParentListEvent(ArrayList<Event> list) {
        children = list;
    }
    @Override
    public List<Event> getChildItemList() {
        return children;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }
}
