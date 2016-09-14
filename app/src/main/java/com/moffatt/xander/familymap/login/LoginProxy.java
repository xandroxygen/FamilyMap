package com.moffatt.xander.familymap.login;

/**
 * A proxy for the login fragment, to be used in the
 * AsyncTask methods.
 * Lets the Async tasks call method on the fragment once request has finished.
 * Created by Xander on 7/27/2016.
 */
public interface LoginProxy {

    void onRequestResponse (RequestResponse response);
}
