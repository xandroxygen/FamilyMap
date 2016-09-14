package com.moffatt.xander.familymap.login;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.model.Filter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Gets all events, and then tests making filters.
 * Created by Xander on 7/28/2016.
 */
public class FilterEventListTest extends InstrumentationTestCase{
    LoginFragment loginFragment;
    RequestController requestController;
    TestCallback testProxy;
    Context context;
    CountDownLatch signal;
    String authToken;
    LoginFragment.REQUEST_TYPE currentRequest;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginFragment = new LoginFragment();
        context = getInstrumentation().getContext();
        signal = new CountDownLatch(1);
        testProxy = new TestCallback(signal);
        authToken = null;
    }

    @Test
    public void testSetEventFilterList() throws Throwable {

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                String username = "x";
                String password = "x";
                String host = "192.168.1.103";
                String port = "8080";
                requestController = new RequestController(host, port, context);

                currentRequest = LoginFragment.REQUEST_TYPE.AUTH;
                requestController.authenticateUser(username, password, testProxy);
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }


    public class TestCallback implements LoginProxy {
        private CountDownLatch signal;

        public TestCallback(CountDownLatch signal) {
            this.signal = signal;
        }

        @Override
        public void onRequestResponse(RequestResponse response) {

            Object responseData = response.getData();

            if (currentRequest == LoginFragment.REQUEST_TYPE.AUTH) {
                String[] responseArray = requestController.decodeLoginResponse((String) responseData);
                authToken = responseArray[0];

                currentRequest = LoginFragment.REQUEST_TYPE.EVENTS;
                requestController.getAllEvents(authToken, testProxy);
            }
            else if (currentRequest == LoginFragment.REQUEST_TYPE.EVENTS) {
                requestController.populateModelWithEvents((String) responseData);
                requestController.constructFilterEventList();
                ArrayList<Filter> filters = FamilyReunion.getInstance().getFilters();
                assertEquals(7, filters.size());
                signal.countDown();
            }

        }
    }
}
