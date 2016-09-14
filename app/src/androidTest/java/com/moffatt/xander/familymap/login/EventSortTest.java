package com.moffatt.xander.familymap.login;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.moffatt.xander.familymap.base.FamilyReunion;
import com.moffatt.xander.familymap.model.Event;
import com.moffatt.xander.familymap.model.Filter;
import com.moffatt.xander.familymap.model.Person;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.hamcrest.*;
import org.hamcrest.CoreMatchers.*;

/**
 * Sorts events chronologically and tests they are in proper order.
 * Birth first, death last.
 * Created by Xander on 8/4/2016.
 */
public class EventSortTest  extends InstrumentationTestCase {
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
    public void testEventSortChronologically() throws Throwable {

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
                requestController.mapPersonsToEvents();
                Person testPerson = FamilyReunion.getInstance().getPeople().get(0);
                ArrayList<Event> testEvents = FamilyReunion.getInstance().getPersonIDToEvents().get(testPerson.getID());

                // test birth first
                Event birth = testEvents.get(0);
                assertEquals("birth", birth.getDescription());
                testEvents.remove(0);

                // test death last
                Event death = testEvents.get(testEvents.size() - 1); // last element
                assertEquals("death", death.getDescription());
                testEvents.remove(testEvents.size() - 1);

                // test all other events chronologically
                String prevYear = "0";
                for (Event event : testEvents) {
                    // TODO: check greater than last year here
                }
                signal.countDown();
            }

        }
    }
}


