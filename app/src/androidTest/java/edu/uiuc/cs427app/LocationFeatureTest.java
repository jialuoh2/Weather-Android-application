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
public class LocationFeatureTest {

    private final String testName = "testLocationFeature";
    private final String testPassword = "testLocationPassword";
    private final UserSelectedLocation testChampaign = new UserSelectedLocation("Champaign", "40.1164204", "-88.2433829");
    private final UserSelectedLocation testChicago = new UserSelectedLocation("Chicago", "41.8781136", "-87.6297982");
    private final String userSelectedLocations = testChampaign.serialize() + "\t" + testChicago.serialize() + "\t";
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final String chicagoLocationName = "Chicago";
    private final String chicagoLatitudeLongitude = "Latitude: 41.8781136, Longitude: -87.6297982";
    private final String champaignLocationName = "Champaign";
    private final String champaignLatitudeLongitude = "Latitude: 40.1164204, Longitude: -88.2433829";
    private UserDBHelper dbLocationF;
    private User userLFTest;

    /**
     * set up test temp db before each test, and insert a fake user with 2 locations in her list.
     */
    @Before
    public void createDb() {
        //set up a test user with 2 locations in her list, Champaign and Chicago
        dbLocationF = new UserDBHelper(context);
        userLFTest = new User();
        userLFTest.setUserName(testName);
        userLFTest.setPassword(testPassword);
        dbLocationF.insertUserData(userLFTest, userSelectedLocations);
    }

    /**
     * close test temp db after each test.
     */
    @After
    public void finish() {
        dbLocationF.removeUserData(userLFTest);
        dbLocationF.close();
    }

    /**
     * This test is used to check location for Chicago
     */
    @Test
    public void checkChicagoLocation() throws InterruptedException {
        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));
        Thread.sleep(1500);

        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(chicagoLocationName)),
                        MyViewAction.clickChildViewWithId(R.id.locationMap)));
        Thread.sleep(3000);

        onView(withId(R.id.map_location_name)).check(matches(withText("Location Name: " + chicagoLocationName)));
        onView(withId(R.id.map_location_latlng)).check(matches(withText(chicagoLatitudeLongitude)));
    }

    /**
     * This test is used to check location for Champaign
     */
    @Test
    public void checkChampaignLocation() throws InterruptedException {
        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));
        Thread.sleep(1500);

        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(champaignLocationName)),
                        MyViewAction.clickChildViewWithId(R.id.locationMap)));
        Thread.sleep(3000);

        onView(withId(R.id.map_location_name)).check(matches(withText("Location Name: " + champaignLocationName)));
        onView(withId(R.id.map_location_latlng)).check(matches(withText(champaignLatitudeLongitude)));
    }

    /**
     * This test is used to check location for Champaign and then Chicago
     */
    @Test
    public void checkChampaignAndChicagoLocation() throws InterruptedException {
        ActivityScenario.launch(new Intent(
                context, MainActivity.class)
                .putExtra("username", testName));
        Thread.sleep(1500);

        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(champaignLocationName)),
                       MyViewAction.clickChildViewWithId(R.id.locationMap)));
        Thread.sleep(3000);

        onView(withId(R.id.map_location_name)).check(matches(withText("Location Name: " + champaignLocationName)));
        onView(withId(R.id.map_location_latlng)).check(matches(withText(champaignLatitudeLongitude)));

        //in the app, click back and open the weather page of Champaign
        pressBack();
        Thread.sleep(1500);


        onView(withId(R.id.locationRecView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(chicagoLocationName)),
                        MyViewAction.clickChildViewWithId(R.id.locationMap)));
        Thread.sleep(3000);

        onView(withId(R.id.map_location_name)).check(matches(withText("Location Name: " + chicagoLocationName)));
        onView(withId(R.id.map_location_latlng)).check(matches(withText(chicagoLatitudeLongitude)));
    }
}
