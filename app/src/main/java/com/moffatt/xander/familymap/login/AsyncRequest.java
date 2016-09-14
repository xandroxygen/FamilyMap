package com.moffatt.xander.familymap.login;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles all API calls via HTTP request.
 * Uses either GET or POST to store and retrieve data.
 * Requests are performed asynchronously.
 * Created by Xander on 7/26/2016.
 */
public class AsyncRequest {

    private static final String HTTP_GET = "GET";
    private static final String HTTP_POST = "POST";

    private String host;
    private String port;
    private String baseURL;

    public AsyncRequest(String host, String port) {
        this.host = host;
        this.port = port;
        this.baseURL = "http://" + host + ":" + port;
    }

    /**
     * Makes GET request to the server.
     * @param url extension to base URL
     * @param headers if necessary
     * @return response - error, data, etc
     * @throws MalformedURLException
     */
    public RequestResponse GET(String url, Map<String, String> headers) throws MalformedURLException {
        try {
            URL requestURL = new URL(baseURL + url);

            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
            connection.setRequestMethod(HTTP_GET);

            for (String header : headers.keySet()) {
                connection.setRequestProperty(header, headers.get(header));
            }
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();

                return new RequestResponse(false, responseToString(responseBody)); // response OK
            }
            else {
                return new RequestResponse(true, "Server returned an error oh noooeee");
            }

        } catch (IOException e) {
            return new RequestResponse(true, e); // other error
        }
    }

    /**
     * Makes POST request to the server and passes up data to store.
     * @param url extension to base url
     * @param postData data to be stored
     * @param headers if necessary
     * @return response - error if one, data, etc
     * @throws MalformedURLException
     */
    public RequestResponse POST(String url, String postData, Map<String, String> headers) throws MalformedURLException {
        try {
            URL requestURL = new URL(baseURL + url);

            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
            connection.setRequestMethod(HTTP_POST);

            for (String header : headers.keySet()) {
                connection.setRequestProperty(header, headers.get(header));
            }
            connection.connect();

            OutputStream requestBody = connection.getOutputStream();
            requestBody.write(postData.getBytes());
            requestBody.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();

                return new RequestResponse(false, responseToString(responseBody)); // response OK
            }
            else {
                return new RequestResponse(true, "Server returned an error oh noooeee");
            }
        } catch (IOException e) {
            return new RequestResponse(true, e); // other error
        }
    }

    /*
        Helper function: changes input stream response to a string for returning.
     */
    private String responseToString(InputStream response) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = response.read(buffer)) != -1) {
            byteStream.write(buffer, 0, length);
        }
        return byteStream.toString();
    }
}
