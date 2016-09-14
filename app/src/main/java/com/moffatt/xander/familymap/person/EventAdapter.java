package com.moffatt.xander.familymap.person;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.model.Person;

import java.util.List;

/**
 * Adapts Event objects to a Recycler View.
 * Created by Xander on 8/1/2016.
 */
public class EventAdapter extends ExpandableRecyclerAdapter<EventAdapter.CardViewHolder, EventAdapter.EventViewHolder> {

    private EventViewHolder childViewHolder;
    private CardViewHolder parentViewHolder;
    private LayoutInflater mInflator;
    private EventClickCallback mCallback;

    public EventAdapter(Context context, List<ParentListEvent> parentItemList) {
        super(parentItemList);
        mCallback = (EventClickCallback) context;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public CardViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View cardView = mInflator.inflate(R.layout.item_card, parentViewGroup, false);
        parentViewHolder = new CardViewHolder(cardView);
        return parentViewHolder;
    }

    @Override
    public EventViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View eventView = mInflator.inflate(R.layout.item_event, childViewGroup, false);
        childViewHolder = new EventViewHolder(eventView);
        return childViewHolder;
    }

    @Override
    public void onBindParentViewHolder(CardViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        ParentListEvent card = (ParentListEvent) parentListItem;
        parentViewHolder.bind(card);
    }

    @Override
    public void onBindChildViewHolder(EventViewHolder childViewHolder, int position, Object childListItem) {
        final Event event = (Event) childListItem;
        childViewHolder.bind(event);
        childViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onEventClick(event.getEventID());
            }
        });
    }

    @Override
    public void setExpandCollapseListener(ExpandCollapseListener expandCollapseListener) {
        super.setExpandCollapseListener(expandCollapseListener);
    }

    // --- VIEW HOLDER CLASSES ---

    public class EventViewHolder extends ChildViewHolder {
        public TextView eventTitle;
        public TextView eventName;
        public View itemView;

        public EventViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
        }

        public void bind(Event event) {
            TextView title = eventTitle;
            TextView name = eventName;

                Person person = FamilyReunion.getInstance().getPersonIDtoPerson().get(event.getPersonID());

                String eventTitle = event.getDescription() + ": " +
                        event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
                title.setText(eventTitle);

                String eventName = person.getFirstName() + " " + person.getLastName();
                name.setText(eventName);
        }
    }

    public class CardViewHolder extends ParentViewHolder {
        public TextView cardTitle;

        public CardViewHolder(View cardView) {
            super(cardView);
            cardTitle = (TextView)cardView.findViewById(R.id.card_title);
        }

        public void bind(ParentListEvent card) {
                String e = "Life Events:";
                cardTitle.setText(e);
        }
    }

    // --- CALLBACK INTERFACE ---
    public interface EventClickCallback {
        void onEventClick(String eventID);
    }
}

