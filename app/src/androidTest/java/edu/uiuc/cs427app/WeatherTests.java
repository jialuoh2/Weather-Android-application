package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uiuc.cs427app.database.user.UserDBHelper;
import edu.uiuc.cs427app.model.User;
import edu.uiuc.cs427app.model.UserSelectedLocation;
import edu.uiuc.cs427app.utils.MyViewAction;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WeatherTests {

    private final String testName = "testNameWeather";
    private final String testPassword = "testWeatherPassword";
    private final UserSelectedLocation testChampaign = new UserSelectedLocation("Champaign", "40.1164204", "-88.2433829");
    private final UserSelectedLocation testChicago = new UserSelectedLocation("Chicago", "41.8781136", "-87.6297982");
    private final String mockUserSelectedLocations = testChampaign.serialize() + "\t" + testChicago.serialize() + "\t";
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private UserDBHelper dbMock;

    /**
     * set up test temp db before each test, and insert a fake user with 2 locations in her list.
     */
    @Before
    public void createDb() {
        //set up a test user with 2 locations in her list, Champaign and Chicago
        dbMock = new UserDBHelper(context);
        User userDao = new User();
        userDao.setUserName(testName);
        userDao.setPassword(testPassword);
        dbMock.insertUserData(userDao, mockUserSelectedLocations);
    }

    /**
     * close test temp db after each test.
     */
    @After
    public void finish() {
        dbMock.close();
    }

    /**
     * This method is used to check weather of two cities - Chicago and Champaign
     */
    @Test
    public void checkChicagoAndChampaignWeather() throws InterruptedException {

        //in the app, start the activity from an existed intent, which provided the user name
        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));

        //in the app, open the weather page of Chicago
        Thread.sleep(1500);

        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Chicago")),
                        MyViewAction.clickChildViewWithId(R.id.locationWeather)));
        Thread.sleep(5000);

        // check the city name "Chicago"
        onView(withId(R.id.city_name_id)).check(matches(withText("Chicago")));

        //in the app, click back and open the weather page of Champaign
        pressBack();
        Thread.sleep(1500);
        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Champaign")),
                        MyViewAction.clickChildViewWithId(R.id.locationWeather)));
        Thread.sleep(5000);

        // check the city name "Champaign"
        onView(withId(R.id.city_name_id)).check(matches(withText("Champaign")));

    }

    /**
     * This method is used to check weather of Chicago
     */
    @Test
    public void checkChicagoWeather() throws InterruptedException {

        //in the app, start the activity from an existed intent, which provided the user name
        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));

        //in the app, open the weather page of Chicago
        Thread.sleep(1500);

        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Chicago")),
                        MyViewAction.clickChildViewWithId(R.id.locationWeather)));
        Thread.sleep(5000);

        // check the city name "Chicago"
        onView(withId(R.id.city_name_id)).check(matches(withText("Chicago")));

    }

    /**
     * This method is used to check weather of Champaign
     */
    @Test
    public void checkChampaignWeather() throws InterruptedException {

        //in the app, start the activity from an existed intent, which provided the user name
        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));

        //in the app, open the weather page of Chicago
        Thread.sleep(1500);

        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Champaign")),
                        MyViewAction.clickChildViewWithId(R.id.locationWeather)));
        Thread.sleep(5000);

        // check the city name "Champaign"
        onView(withId(R.id.city_name_id)).check(matches(withText("Champaign")));

    }
}