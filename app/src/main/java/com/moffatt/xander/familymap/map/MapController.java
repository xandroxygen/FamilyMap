package com.moffatt.xander.familymap.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.text.TextPaint;
import android.widget.IconTextView;
import android.widget.TextView;

import com.amazon.geo.mapsv2.AmazonMap;
import com.amazon.geo.mapsv2.CameraUpdateFactory;
import com.amazon.geo.mapsv2.model.BitmapDescriptorFactory;
import com.amazon.geo.mapsv2.model.LatLng;
import com.amazon.geo.mapsv2.model.Marker;
import com.amazon.geo.mapsv2.model.MarkerOptions;
import com.amazon.geo.mapsv2.model.Polyline;
import com.amazon.geo.mapsv2.model.PolylineOptions;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.model.Filter;
import com.moffatt.xander.familymap.model.Line;
import com.moffatt.xander.familymap.model.LineSetting;
import com.moffatt.xander.familymap.model.MapSetting;
import com.moffatt.xander.familymap.model.Person;
import com.moffatt.xander.familymap.model.Setting;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Controls all aspects of the map fragment.
 * Displays event data, responds to user input.
 * Created by Xander on 7/30/2016.
 */
public class MapController {
    private MapFragment mapView;
    private ArrayList<Marker> mapMarkers;
    private ArrayList<Polyline> currentMapLines;
    private AmazonMap theMap;
    private Context context;
    private String selectedEventID;

    public MapController(MapFragment m, AmazonMap map) {
        mapView = m;
        theMap = map;
        context = mapView.getContext();
        mapMarkers = new ArrayList<>();
        currentMapLines = new ArrayList<>();
    }

    /**
     * Initializes map data.
     * Draws all events, based on filter filter.
     */
    public void initializeMapElements() {
        drawAllEvents();
    }

    /**
     * Changes map display type -
     * Normal, Satellite, Hybrid, or Terrain.
     * Reads setting type and changes map to match.
     * @param type map type contained in setting.
     */
    public void changeMapType(Setting.MAP_TYPE type) {
        switch (type) {
            case NORMAL:
                theMap.setMapType(AmazonMap.MAP_TYPE_NORMAL);
                break;
            case HYBRID:
                theMap.setMapType(AmazonMap.MAP_TYPE_HYBRID);
                break;
            case TERRAIN:
                theMap.setMapType(AmazonMap.MAP_TYPE_TERRAIN);
                break;
            case SATELLITE:
                theMap.setMapType(AmazonMap.MAP_TYPE_SATELLITE);
                break;
            default:
                theMap.setMapType(AmazonMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    /**
     * Responds to click on event:
     * centers camera, and displays event info on card.
     */
    public void onEventClick(String eventID) {

        selectedEventID = eventID;
        Event event = FamilyReunion.getInstance().getEventIDtoEvent().get(eventID);
        LatLng eventPos = new LatLng(event.getLatitude(), event.getLongitude());
        theMap.animateCamera(CameraUpdateFactory.newLatLng(eventPos));

        updateMapLines(event);

        changeCardText(event);
    }

    /**
     * Called when event is selected. Searches events for ID,
     * and updates the info card text with name and event details/
     * @param event Event model
     */
    private void changeCardText(Event event) {
        CardView card = mapView.getInfoCard();
        TextView nameView = (TextView)card.findViewById(R.id.card_name);
        TextView infoView = (TextView)card.findViewById(R.id.card_event);
        IconTextView iconView = (IconTextView)card.findViewById(R.id.card_icon);

        Person person = FamilyReunion.getInstance().getPersonIDtoPerson().get(event.getPersonID());
        String name = person.getFirstName() + " " + person.getLastName();
        nameView.setText(name);

        String eventInfo = event.getDescription() + ": " +
                event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
        infoView.setText(eventInfo);

        if (person.getGender().equals("m")) {
            iconView.setText(R.string.info_card_icon_male);
        }
        else {
            iconView.setText(R.string.info_card_icon_female);
        }
        for (Filter filter : FamilyReunion.getInstance().getFilters()) {
            if (filter.getDescription().equals(event.getDescription())) {
                iconView.setTextColor(filter.getColor());
            }
        }
    }

    public String getSelectedEventID() {
        return selectedEventID;
    }

    /**
     * Handles drawing of all events to the screen.
     * Bases which events to draw on selected filters.
     */
    private void drawAllEvents() {
        Map<String, Bitmap> filterIcons = new HashMap<>();

        for (Filter filter : FamilyReunion.getInstance().getFilters()) {
                Bitmap icon = iconifyToBitmap(Iconify.IconValue.fa_map_marker, filter.getColor());
                filterIcons.put(filter.getDescription().toLowerCase(), icon);
        }

        for (Event event : FamilyReunion.getInstance().getFilteredEvents()) {
            Bitmap icon = filterIcons.get(event.getDescription().toLowerCase());
            String id = event.getEventID();
            LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .snippet(id)
                    .icon(BitmapDescriptorFactory.fromBitmap(icon));
            Marker marker = theMap.addMarker(options);
            mapMarkers.add(marker);
        }
    }

    /**
     * Takes a font awesome icon and a color, and returns a bitmap
     */
    private Bitmap iconifyToBitmap(Iconify.IconValue iconValue, int color) {
        Drawable iconDrawable = iconifyToDrawable(Iconify.IconValue.fa_map_marker, color);
        Bitmap icon = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);
        iconDrawable.draw(canvas);
        return icon;
    }

    /**
     * Takes in a font awesome icon, and a color, and returns a filled Drawable.
     */
    private Drawable iconifyToDrawable(Iconify.IconValue iconValue, final int color) {
        Drawable icon = new IconDrawable(context, Iconify.IconValue.fa_map_marker) {
            @Override
            public void draw(Canvas canvas) {
                // The TextPaint is defined in the constructor
                // but we override it here
                TextPaint paint = new TextPaint();
                paint.setTypeface(Iconify.getTypeface(mapView.getContext()));
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setUnderlineText(false);

                // If you need a custom color specify it here
                paint.setColor(color);

                paint.setAntiAlias(true);
                paint.setTextSize(getBounds().height());
                Rect textBounds = new Rect();
                String textValue = String.valueOf(Iconify.IconValue.fa_map_marker.character());
                paint.getTextBounds(textValue, 0, 1, textBounds);
                float textBottom = (getBounds().height() - textBounds.height()) / 2f + textBounds.height() - textBounds.bottom;
                canvas.drawText(textValue, getBounds().width() / 2f, textBottom, paint);
            }

        }.sizeDp(40);
        return icon;
    }

    /**
     * Handles drawing of lines for current person to the screen.
     * Bases which lines to draw on line filter.
     * Removes lines previously drawn, if any.
     * TODO: fix these dang lines, all of them. Spouse is correct and that's it.
     */
    private void updateMapLines(Event event) {

        for (Polyline p : currentMapLines) {
            p.remove();
        }

        ArrayList<Setting> settings = FamilyReunion.getInstance().getSettings();
        LineSetting lifeLineSetting = (LineSetting) settings.get(0);
        LineSetting familyLineSetting = (LineSetting) settings.get(1);
        LineSetting spouseLineSetting = (LineSetting) settings.get(2);

        Person person = FamilyReunion.getInstance().getPersonIDtoPerson().get(event.getPersonID());

        drawSpouseLines(event, person, spouseLineSetting);

        drawLifeStoryLines(lifeLineSetting, person);

        if (familyLineSetting.isDrawn()) {
            drawFamilyTreeLines(event, 10f, familyLineSetting.getColor());
        }
    }

    /**
     * Helper function: draws spouse line if setting is on.
     * @param event selected event
     * @param person current person
     * @param spouseLineSetting
     */
    public void drawSpouseLines(Event event, Person person, LineSetting spouseLineSetting) {
        ArrayList<LatLng> points = new ArrayList<>();
        if (person.getSpouseID() != null) {
            Person spouse = FamilyReunion.getInstance().getPersonIDtoPerson().get(person.getSpouseID());
            String destID = FamilyReunion.getInstance().findBirthOrMostRecentEvent(spouse);
            Event spouseEvent = FamilyReunion.getInstance().getEventIDtoEvent().get(destID);

            if (event != null && spouseEvent != null) {
                points.add(new LatLng(event.getLatitude(), event.getLongitude()));
                points.add(new LatLng(spouseEvent.getLatitude(), spouseEvent.getLongitude()));
            }

            if (spouseLineSetting.isDrawn()) {
                PolylineOptions options = new PolylineOptions()
                        .addAll(points)
                        .color(spouseLineSetting.getColor());

                Polyline spousePolyline = theMap.addPolyline(options);
                currentMapLines.add(spousePolyline);
            }
        }
    }

    public void drawLifeStoryLines(LineSetting lifeLineSetting, Person person) {
        ArrayList<LatLng> points = new ArrayList<>();

        for (Event event : FamilyReunion.getInstance().getPersonIDToEvents().get(person.getID())) {
            points.add(new LatLng(event.getLatitude(), event.getLongitude()));
        }

        if (lifeLineSetting.isDrawn()) {
            PolylineOptions options = new PolylineOptions()
                    .addAll(points)
                    .color(lifeLineSetting.getColor());

            Polyline storyPolyLine = theMap.addPolyline(options);
            currentMapLines.add(storyPolyLine);
        }
    }

    /**
     * Recursive helper function for drawing lines.
     * @param eventID
     * @param isFather
     * @param lineWidth
     */
    public void drawFamilyTreeLines2(String eventID, boolean isFather, float lineWidth) {

        Event event = FamilyReunion.getInstance().getEventIDtoEvent().get(eventID);
        Person person = FamilyReunion.getInstance().getPersonIDtoPerson().get(event.getPersonID());
        ArrayList<LatLng> points = new ArrayList<>();

        if (isFather) {

            Person father = FamilyReunion.getInstance().getPersonIDtoPerson().get(person.getFatherID());
            Event fatherEvent = null;
            if (father != null) {
                String fatherEventID = FamilyReunion.getInstance().findBirthOrMostRecentEvent(father);
                fatherEvent = FamilyReunion.getInstance().getEventIDtoEvent().get(fatherEventID);
            }

            if (event != null && fatherEvent != null) {
                points.add(new LatLng(event.getLatitude(), event.getLongitude()));
                points.add(new LatLng(fatherEvent.getLatitude(), fatherEvent.getLongitude()));

                LineSetting lineSetting = (LineSetting) FamilyReunion.getInstance().getSettings().get(1);

                if (lineSetting.isDrawn()) {
                    PolylineOptions options = new PolylineOptions()
                            .addAll(points)
                            .width(lineWidth)
                            .color(lineSetting.getColor());

                    Polyline polyline = theMap.addPolyline(options);
                    currentMapLines.add(polyline);
                }

                if (lineWidth > 1.0f) {
                    lineWidth = lineWidth - .5f;
                }


            }
        }
        else { // mother

            Person mother = FamilyReunion.getInstance().getPersonIDtoPerson().get(person.getFatherID());
            Event motherEvent = null;
            if (mother != null) {
                String motherEventID = FamilyReunion.getInstance().findBirthOrMostRecentEvent(mother);
                motherEvent = FamilyReunion.getInstance().getEventIDtoEvent().get(motherEventID);
            }

            if (event != null && motherEvent != null) {
                points.add(new LatLng(event.getLatitude(), event.getLongitude()));
                points.add(new LatLng(motherEvent.getLatitude(), motherEvent.getLongitude()));

                LineSetting lineSetting = (LineSetting) FamilyReunion.getInstance().getSettings().get(1);

                if (lineSetting.isDrawn()) {
                        PolylineOptions options = new PolylineOptions()
                                .addAll(points)
                                .width(lineWidth)
                                .color(lineSetting.getColor());

                        Polyline polyline = theMap.addPolyline(options);
                        currentMapLines.add(polyline);
                }

                if (lineWidth > 1.0f) {
                    lineWidth = lineWidth - .5f;
                }

            }
        }
    }

    public void drawFamilyTreeLines(Event event, float lineWidth, int color) {

        Person person = FamilyReunion.getInstance().getPersonIDtoPerson().get(event.getPersonID());

        if (person.getFatherID() != null) {
            Person father = FamilyReunion.getInstance().getPersonIDtoPerson().get(person.getFatherID());
            String fEventID = FamilyReunion.getInstance().findBirthOrMostRecentEvent(father);
            if (fEventID != null) {
                Event fEvent = FamilyReunion.getInstance().getEventIDtoEvent().get(fEventID);
                ArrayList<LatLng> points = new ArrayList<>();
                points.add(new LatLng(event.getLatitude(), event.getLongitude()));
                points.add(new LatLng(fEvent.getLatitude(), fEvent.getLongitude()));

                PolylineOptions options = new PolylineOptions()
                        .addAll(points)
                        .width(lineWidth)
                        .color(color);

                Polyline polyline = theMap.addPolyline(options);
                currentMapLines.add(polyline);

                float fLineWidth = lineWidth;
                if (lineWidth > 1.0f) {
                    fLineWidth = lineWidth * .67f;
                }

                drawFamilyTreeLines(fEvent, fLineWidth, color);
            }

        }
        if (person.getMotherID() != null) {
            Person mother = FamilyReunion.getInstance().getPersonIDtoPerson().get(person.getMotherID());
            String mEventID = FamilyReunion.getInstance().findBirthOrMostRecentEvent(mother);
            if (mEventID != null) {
                Event mEvent = FamilyReunion.getInstance().getEventIDtoEvent().get(mEventID);
                ArrayList<LatLng> points = new ArrayList<>();
                points.add(new LatLng(event.getLatitude(), event.getLongitude()));
                points.add(new LatLng(mEvent.getLatitude(), mEvent.getLongitude()));

                PolylineOptions options = new PolylineOptions()
                        .addAll(points)
                        .width(lineWidth)
                        .color(color);

                Polyline polyline = theMap.addPolyline(options);
                currentMapLines.add(polyline);

                float mLineWidth = lineWidth;
                if (lineWidth > 1.0f) {
                    mLineWidth = lineWidth * .67f;
                }

                drawFamilyTreeLines(mEvent, mLineWidth, color);
            }
        }
    }
}
