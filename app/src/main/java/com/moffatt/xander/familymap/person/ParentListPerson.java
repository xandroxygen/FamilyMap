package com.moffatt.xander.familymap.person;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.moffatt.xander.familymap.model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for parent list of persons
 * Created by Xander on 8/2/2016.
 */
public class ParentListPerson implements ParentListItem {

    ArrayList<Person> children;

    public ParentListPerson(ArrayList<Person> list) {
        children = list;
    }

    @Override
    public List<Person> getChildItemList() {
        return children;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }
}
