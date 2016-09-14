package com.moffatt.xander.familymap.login;

/**
 * Simple wrapper class for response of HTTP requests.
 * Contains error if any, data, and an error flag.
 * Created by Xander on 7/26/2016.
 */
public class RequestResponse {

    private boolean hasError;
    private Exception error;
    private Object data;

    public RequestResponse(boolean hasError, Object data) {
        this.hasError = hasError;
        this.data = data;
    }

    public RequestResponse(boolean hasError, Exception error) {
        this.hasError = hasError;
        this.data = "";
        this.error = error;
    }

    public boolean hasError() { return hasError; }

    public Exception getError() { return error; }

    public Object getData() { return data; }
}
