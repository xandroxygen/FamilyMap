package com.moffatt.xander.familymap.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amazon.geo.mapsv2.AmazonMap;
import com.amazon.geo.mapsv2.OnMapReadyCallback;
import com.amazon.geo.mapsv2.SupportMapFragment;
import com.amazon.geo.mapsv2.model.BitmapDescriptor;
import com.amazon.geo.mapsv2.model.BitmapDescriptorFactory;
import com.amazon.geo.mapsv2.model.LatLng;
import com.amazon.geo.mapsv2.model.Marker;
import com.amazon.geo.mapsv2.model.MarkerOptions;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.base.MainActivity;
import com.moffatt.xander.familymap.model.Line;
import com.moffatt.xander.familymap.model.MapSetting;

import java.util.ArrayList;

/**
 * Fragment that displays an Amazon map.
 * Created by Xander on 7/27/2016.
 */
public class MapFragment extends Fragment {

    private SupportMapFragment mapFragment;
    private AmazonMap map;
    private View mapView;
    private MapController controller;
    private OnMapChangeActivityListener mCallback;
    private CardView infoCard;
    private ProgressDialog dialog;
    private boolean isActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapView = inflater.inflate(R.layout.fragment_map, container, false);

        isActivity = false;

        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.actual_map));
        final MapFragment parentFragment = this;
        mapFragment.getMapAsync(
                new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(AmazonMap amazonMap) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            map = amazonMap;
                            controller = new MapController(parentFragment, map);
                            controller.initializeMapElements();

                            MapSetting mapSetting = (MapSetting) FamilyReunion.getInstance().getSettings().get(3);
                            controller.changeMapType(mapSetting.getType());

                            mCallback.onMapLoad();

                            map.setOnMarkerClickListener(new AmazonMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    controller.onEventClick(marker.getSnippet());
                                    return true;
                                }
                            });
                    }
                }
        );

        infoCard = (CardView)mapView.findViewById(R.id.info_card);
        infoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller != null && controller.getSelectedEventID() != null) {
                    mCallback.onCardClick(controller.getSelectedEventID());
                }
            }
        });

        return mapView;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public void setIsActivity(boolean activity) {
        isActivity = activity;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem search = menu.findItem(R.id.search_icon);
        if (search != null) {
            search.setIcon(new IconDrawable(getContext(), Iconify.IconValue.fa_search)
                        .colorRes(android.R.color.white)
                        .actionBarSize()).setVisible(true);
        }

        MenuItem filter = menu.findItem(R.id.filter_icon);
        if (filter != null) {
            filter.setIcon(new IconDrawable(getContext(), Iconify.IconValue.fa_filter)
                        .colorRes(android.R.color.white)
                        .actionBarSize()).setVisible(true);
        }

        MenuItem settings = menu.findItem(R.id.settings_icon);
        if (settings != null) {
            settings.setIcon(new IconDrawable(getContext(), Iconify.IconValue.fa_gear)
                    .colorRes(android.R.color.white)
                    .actionBarSize()).setVisible(true);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (OnMapChangeActivityListener) context;
        dialog = mCallback.getLoadingDialog();
        if (dialog != null) {
            dialog.setMessage("Loading map...");
        }

    }


    public void showCenteredToast(String toastText) {
        Toast toast = Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public CardView getInfoCard() {
        return infoCard;
    }

    public void centerOnEvent(String eventID) {
        controller.onEventClick(eventID);
    }

    /**
     * Communicates with parent activity when activity needs to be changed.
     */
    public interface OnMapChangeActivityListener {

        ProgressDialog getLoadingDialog();

        /**
         * Responds to click on info card:
         * (eventually) opens new Person Activity.
         * Valid for all parent activities.
         */
        public void onCardClick(String eventID);

        public void onMapLoad();
    }
}
