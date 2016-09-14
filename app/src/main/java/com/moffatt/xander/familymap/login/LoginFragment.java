package com.moffatt.xander.familymap.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moffatt.xander.familymap.R;
import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.base.MainActivity;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.model.Filter;
import com.moffatt.xander.familymap.model.LineSetting;
import com.moffatt.xander.familymap.model.MapSetting;
import com.moffatt.xander.familymap.model.Person;
import com.moffatt.xander.familymap.model.Setting;
import com.moffatt.xander.familymap.model.User;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Login Fragment: handles all API calls to login and fill datastore,
 * using RequestController to make requests and initialize data.
 * Once authenticated and done, returns to main activity to start map fragment.
 */
public class LoginFragment extends Fragment
        implements LoginProxy {

    OnSuccessfulLoginListener mCallback;

    public enum REQUEST_TYPE { AUTH, PEOPLE, EVENTS };
    REQUEST_TYPE currentRequest;

    RequestController controller;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText serverEditText;
    EditText portEditText;

    public LoginFragment() {
        currentRequest = REQUEST_TYPE.AUTH;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View loginView = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = (Button)loginView.findViewById(R.id.login_button);

        usernameEditText = (EditText)loginView.findViewById(R.id.username);
        passwordEditText = (EditText)loginView.findViewById(R.id.password);
        serverEditText = (EditText)loginView.findViewById(R.id.host);
        portEditText = (EditText)loginView.findViewById(R.id.port);

        return loginView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.option_icon_group).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Called by Main Activity's button listener.
     * Reads user input, creates RequestController, and calls authenticate user.
     */
    public void onUserLogin() {
        currentRequest = REQUEST_TYPE.AUTH; // is this reset needed?

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        String host = serverEditText.getText().toString();
        String port = portEditText.getText().toString();

        boolean networkExists = true;

        if (checkIfInputValid(username, password, host, port)) {
            controller = new RequestController(host, port, getContext());

            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(usernameEditText.getApplicationWindowToken(), 0);

            networkExists = controller.authenticateUser(username, password, this);
            if(!networkExists) {
                showCenteredToast("There is no network to connect to.");
            }
        }
        else {
            showCenteredToast("All fields must be filled to log in.");
        }
    }

    /**
     * Handles request responses for 3 requests:
     * log in user, get all people, and get all events.
     * After these are finished, initializes data via controller.
     * @param response
     */
    @Override
    public void onRequestResponse(RequestResponse response) {
        if (currentRequest == REQUEST_TYPE.AUTH) {
            if (response.hasError()) {
                showCenteredToast("Login failed: There was an unknown error");
            }
            else {
                Object responseData = response.getData();
                String[] decodedResponse = controller.decodeLoginResponse((String) responseData);

                if (decodedResponse[0].equals("invalid")) {
                    displayLoginResponse(false);
                }
                else {
                    // before anything, clear out old data (including user login data)
                    FamilyReunion.getInstance().clear(true);

                    FamilyReunion.getInstance().setController(controller);

                    FamilyReunion.getInstance().getCurrentUser().setUsername(usernameEditText.getText().toString());
                    FamilyReunion.getInstance().getCurrentUser().setPassword(passwordEditText.getText().toString());
                    FamilyReunion.getInstance().getCurrentUser().setAuthToken(decodedResponse[0]);
                    FamilyReunion.getInstance().getCurrentUser().setID(decodedResponse[2]);
                }

                controller.getAllPeople(FamilyReunion.getInstance().getCurrentUser().getAuthToken(), this);

                currentRequest = REQUEST_TYPE.PEOPLE;
            }
        }
        else if (currentRequest == REQUEST_TYPE.PEOPLE) {

            if (response.hasError()) {
                showCenteredToast("Downloading persons file failed: there was an unknown error.");
            }
            else {
                Object responseData = response.getData();

                controller.populateModelWithPersons((String) responseData);

                controller.getAllEvents(FamilyReunion.getInstance().getCurrentUser().getAuthToken(), this);

                currentRequest = REQUEST_TYPE.EVENTS;
            }
        }
        else if (currentRequest == REQUEST_TYPE.EVENTS) {

            if (response.hasError()) {
                showCenteredToast("Downloading events file failed: there was an unknown error.");
            }
            else {
                Object responseData = response.getData();

                controller.populateModelWithEvents((String) responseData);

                // initialize the rest of the model
                controller.mapPersonsToEvents();

                controller.mapChildrenToParents();

                controller.constructDefaultSettings();

                controller.constructFilterEventList();

                controller.populateFiltersWithEvents();

                mCallback.onSuccessfulLogin();

                currentRequest = REQUEST_TYPE.AUTH;
            }
        }
    }

    // --- REQUEST HELPERS --- //

    private boolean checkIfInputValid(String u, String pw, String h, String po) {
        if (!u.isEmpty() && !pw.isEmpty() && !h.isEmpty() && !po.isEmpty()) {
            return true;
        }
        return false;
    }

    private void displayLoginResponse(boolean loginStatus) {
        if (!loginStatus) {
            showCenteredToast("Login failed: Invalid username/password");
        }
        else {
            showCenteredToast("Login OK, Name: " + FamilyReunion.getInstance().getCurrentUser().getFirstName()
                    + " " + FamilyReunion.getInstance().getCurrentUser().getLastName());
        }
    }

    private void showCenteredToast(String toastText) {
        Toast toast = Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    /**
     * Used for communication with the Main Activity.
     * This is called when user has logged in and data has been synced.
     */
    public interface OnSuccessfulLoginListener {
        void onSuccessfulLogin();
    }
}