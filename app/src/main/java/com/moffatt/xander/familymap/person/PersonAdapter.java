package com.moffatt.xander.familymap.person;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.model.Person;

import java.util.List;

/**
 * Adapts Person objects to a Recycler View.
 * Created by Xander on 8/2/2016.
 */
public class PersonAdapter extends ExpandableRecyclerAdapter<PersonAdapter.CardViewHolder, PersonAdapter.PersonViewHolder>{

    private PersonViewHolder childViewHolder;
    private CardViewHolder parentViewHolder;
    private LayoutInflater mInflator;
    private PersonClickCallback mCallback;
    private Person selectedPerson;

    public PersonAdapter(Context context, List<ParentListPerson> parentItemList, Person selectedPerson) {
        super(parentItemList);
        mCallback = (PersonClickCallback) context;
        this.selectedPerson = selectedPerson;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public CardViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View cardView = mInflator.inflate(R.layout.item_card, parentViewGroup, false);
        parentViewHolder = new CardViewHolder(cardView, selectedPerson);
        return parentViewHolder;
    }

    @Override
    public PersonViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View personView = mInflator.inflate(R.layout.item_event, childViewGroup, false);
        childViewHolder = new PersonViewHolder(personView, selectedPerson);
        return childViewHolder;
    }

    @Override
    public void onBindParentViewHolder(CardViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        ParentListPerson card = (ParentListPerson) parentListItem;
        parentViewHolder.bind(card);
    }

    @Override
    public void onBindChildViewHolder(PersonViewHolder childViewHolder, int position, Object childListItem) {
        final Person person = (Person) childListItem;
        childViewHolder.bind(person);
        childViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onPersonClick(person.getID());
            }
        });
    }

    // --- VIEW HOLDER CLASSES ---

    public class CardViewHolder extends ParentViewHolder {
        public TextView cardTitle;
        public Person selectedPerson;

        public CardViewHolder(View itemView, Person selectedPerson) {
            super(itemView);
            this.selectedPerson = selectedPerson;
            cardTitle = (TextView)itemView.findViewById(R.id.card_title);
        }

        public void bind(ParentListPerson card) {
            String p;
            if (selectedPerson != null) {
                p = "Family Members:";
            }
            else {
                p = "People:";
            }
            cardTitle.setText(p);
        }
    }

    public class PersonViewHolder extends ChildViewHolder {
        public TextView personName;
        public TextView personRelation;
        public IconTextView personIcon;
        public View itemView;
        private Person selectedPerson;

        public PersonViewHolder(View itemView, Person selectedPerson) {
            super(itemView);
            this.selectedPerson = selectedPerson;
            this.itemView = itemView;

            personName = (TextView) itemView.findViewById(R.id.event_title);
            personRelation = (TextView) itemView.findViewById(R.id.event_name);
            personIcon = (IconTextView) itemView.findViewById(R.id.event_icon);
        }

        public void bind(Person person) {

            String name = person.getFirstName() + " " + person.getLastName();

            int iconText;
            if (person.getGender().equals("m")) {
                iconText = R.string.info_card_icon_male;
            }
            else {
                iconText = R.string.info_card_icon_female;
            }

            String relation;
            if (selectedPerson != null) {
                if (person.getID().equals(selectedPerson.getFatherID())) {
                    relation = "Father";
                } else if (person.getID().equals(selectedPerson.getMotherID())) {
                    relation = "Mother";
                } else {
                    relation = "Child";
                }
            }
            else {
                relation = "";
            }

            personName.setText(name);
            personRelation.setText(relation);
            personIcon.setText(iconText);
        }
    }

    // --- CALLBACK INTERFACE ---
    public interface PersonClickCallback {
        void onPersonClick(String personID);
    }
}
