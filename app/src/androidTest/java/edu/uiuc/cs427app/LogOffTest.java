package edu.uiuc.cs427app;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uiuc.cs427app.database.user.UserDBHelper;
import edu.uiuc.cs427app.model.User;

@RunWith(AndroidJUnit4.class)
public class LogOffTest {

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Context targetContext = InstrumentationRegistry.getInstrumentation()
            .getTargetContext();
    private final String testName = "testLogOff";
    private final String testPassword = "testPassword";
    private UserDBHelper dbMock;


    /**
     * set test user in database
     */
    @Before
    public void createDb() {
        dbMock = new UserDBHelper(context);
        User userAlex = new User();
        userAlex.setUserName(testName);
        userAlex.setPassword(testPassword);
        dbMock.insertUserData(userAlex, "");
    }

    /**
     * close test temp db after each test.
     */
    @After
    public void finish() {
        dbMock.close();
    }

    /**
     * To test user log off feature
     *
     * @throws InterruptedException
     */
    @Test
    public void testLogOffSuccess() throws InterruptedException {
        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));
        Thread.sleep(2000);

        onView(withId(R.id.logoutButton)).perform(click());
        Thread.sleep(2000);

        String s = targetContext.getResources().getString(R.string.city_application);
        onView(withId(R.id.user_login_welcome_text)).check(matches(withText(s)));
    }
}
