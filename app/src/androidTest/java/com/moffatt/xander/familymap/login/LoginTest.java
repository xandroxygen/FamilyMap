package com.moffatt.xander.familymap.login;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Tests login function for bad and good logins.
 * Created by Xander on 7/28/2016.
 */
public class LoginTest extends InstrumentationTestCase {

    LoginFragment loginFragment;
    RequestController requestController;
    TestCallback testProxy;
    Context context;
    CountDownLatch signal;
    String username;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginFragment = new LoginFragment();
        context = getInstrumentation().getContext();
        signal = new CountDownLatch(1);
        testProxy = new TestCallback(signal);
        username = "x";

    }

    @Test
    public void testSuccessfulLogin() throws Throwable{

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                String password = "x";
                String host = "192.168.1.103";
                String port = "8080";
                requestController = new RequestController(host, port, context);

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
            signal.countDown();
            Object responseData = response.getData();
            String[] responseArray = requestController.decodeLoginResponse((String) responseData);

            assertEquals(username, responseArray[1]);
        }
    }
}
